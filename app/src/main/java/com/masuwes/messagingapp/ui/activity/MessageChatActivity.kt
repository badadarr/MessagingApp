package com.masuwes.messagingapp.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.Chat
import com.masuwes.messagingapp.model.Users
import com.masuwes.messagingapp.ui.Utils
import com.masuwes.messagingapp.ui.adapter.ChatAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {

    private var userVisitId: String = ""
    private var firebaseUser: FirebaseUser? = null
    private lateinit var chatAdapter: ChatAdapter
    private var chatList = arrayListOf<Chat>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        setSupportActionBar(findViewById(R.id.toolbar_contact_profile))
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }


        recyclerView = findViewById(R.id.rv_chat)
        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@MessageChatActivity)
            linearLayoutManager.stackFromEnd = true
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }

        intent = intent
        userVisitId = intent.getStringExtra(Utils.PACKAGE).toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = userVisitId.let {
            FirebaseDatabase.getInstance().reference
                .child(Utils.USER_TABLE).child(it)
        }
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)

                tv_username_on_chat.text = user?.username
                Picasso.get().load(user?.profile).into(profile_image_on_chat)

                retrieveMessages(firebaseUser!!.uid, userVisitId, user?.profile)

            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error message : $error")
            }

        })

        btn_send.setOnClickListener {
            val message = edt_chat_box.text.toString()
            if (message.isBlank()) {
                Toast.makeText(this, "Write message first", Toast.LENGTH_SHORT).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userVisitId, message)
            }
            edt_chat_box.setText("")
        }

        add_media.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), Utils.REQUEST_CODE)
        }

        seenMessage(userVisitId)
    }

    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child(Utils.CHATS_TABLE)
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child(Utils.CHAT_LIST)
                        .child(firebaseUser!!.uid)
                        .child(userVisitId)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(userVisitId)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child(Utils.CHAT_LIST)
                                .child(userVisitId)
                                .child(firebaseUser!!.uid)

                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            showToast("Error message : $error")
                        }

                    })


                    // implement fcm notification



                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Image sending...")
            progressDialog.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child(Utils.CHAT_IMAGE_TABLE)
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask { task ->
                if (task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask filePath.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser?.uid
                    messageHashMap["message"] = "sent you an image"
                    messageHashMap["receiver"] = userVisitId
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child(Utils.CHATS_TABLE).child(messageId!!).setValue(messageHashMap)

                    progressDialog.dismiss()
                }
            }
        }
    }

    lateinit var seenListener: ValueEventListener
    private fun seenMessage(userId: String) {
        reference = FirebaseDatabase.getInstance().reference.child(Utils.CHATS_TABLE)

        seenListener = reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot in snapshot.children) {
                    val data = dataSnapshot.getValue(Chat::class.java)

                    if (data?.receiver.equals(firebaseUser?.uid) && data?.sender.equals(userId)) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error message : $error")
            }

        })
    }

    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        reference = FirebaseDatabase.getInstance().reference.child(Utils.CHATS_TABLE)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList).clear() // TODO = "need to fix"
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(senderId) && chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId) && chat.sender.equals(senderId)) {
                        (chatList).add(chat)
                    }
                    chatAdapter = ChatAdapter(this@MessageChatActivity, (chatList), receiverImageUrl!!)
                    recyclerView.adapter = chatAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error message : $error")
            }

        })

    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
    }


}


















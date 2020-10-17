package com.masuwes.messagingapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.ChatList
import com.masuwes.messagingapp.model.Users
import com.masuwes.messagingapp.notification.Token
import com.masuwes.messagingapp.ui.Utils
import com.masuwes.messagingapp.ui.adapter.UserAdapter
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private val mUsers = arrayListOf<Users>()
    private val userChatList = arrayListOf<ChatList>()
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_chat_list
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val ref = FirebaseDatabase.getInstance().reference
            .child(Utils.CHAT_LIST)
            .child(firebaseUser.uid)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userChatList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val chatList = dataSnapshot.getValue(ChatList::class.java)
                        userChatList.add(chatList!!)
                    }
                    retrieveChatList()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error message : $error", Toast.LENGTH_LONG).show()
                }

            })

        updateToken(FirebaseInstanceId.getInstance().token)

    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child(Utils.TOKENS_TABLE)
        val mToken = Token(token)
        ref.child(firebaseUser.uid).setValue(mToken)
    }

    private fun retrieveChatList() {

        val refUsers = FirebaseDatabase.getInstance().reference.child(Utils.USER_TABLE)
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUsers.clear()

                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in userChatList) {
                        if (user?.uid.equals(eachChatList.id)) {
                            mUsers.add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers, true)
                recyclerView.adapter = userAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}





















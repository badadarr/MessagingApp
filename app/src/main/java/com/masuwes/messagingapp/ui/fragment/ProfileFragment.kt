package com.masuwes.messagingapp.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.Users
import com.masuwes.messagingapp.ui.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageRef: StorageReference
    private lateinit var imageUri: Uri
    private var coverChecker: String? = null
    private var socialChecker: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().reference.child(Utils.USER_TABLE).child(firebaseUser.uid)
        storageRef = FirebaseStorage.getInstance().reference.child(Utils.USER_IMAGE_TABLE)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)

                    if (context != null) {
                        tv_username_profile.text = user?.username
                        tv_status_profile.text = user?.status
                        Picasso.get().load(user?.profile).into(image_profile)
                        Picasso.get().load(user?.cover).into(poster_profile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        change_profile.setOnClickListener(this)
        poster_profile.setOnClickListener(this)
        instagram.setOnClickListener(this)
        twitter.setOnClickListener(this)
        linkedin.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.change_profile -> pickImage()
            R.id.poster_profile -> {
                coverChecker = "cover"
                pickImage()
            }
            R.id.instagram -> {
                socialChecker = "instagram"
                setSocialLink()
            }
            R.id.twitter -> {
                socialChecker = "twitter"
                setSocialLink()
            }
            R.id.linkedin -> {
                socialChecker = "website"
                setSocialLink()
            }
        }
    }

    private fun setSocialLink() {
        val builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker == "website") {
            builder.setTitle("Write Url : ")
        } else {
            builder.setTitle("Write username : ")
        }

        val editText = EditText(context)

        if (socialChecker == "website") {
            editText.hint = "e.g www.website.com"
        } else {
            editText.hint = "e.g newuser6371"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()

            if (str.isBlank()) {
                showToast("Please write something...")
            } else {
                saveSocialLink(str)
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when(socialChecker) {
            "instagram" -> {
                mapSocial["instagram"] = "https://www.instagram.com/$str"
            }
            "twitter" -> {
                mapSocial["twitter"] = "https://twitter.com/$str"
            }
            "website" -> {
                mapSocial["website"] = "https://$str"
            }
        }

        reference.updateChildren(mapSocial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("saved success")
            }
        }.addOnFailureListener { error ->
            showToast("$error")
        }

    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Utils.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data!!
            showToast("Uploading...")
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait")
        progressBar.show()


        val fileRef = storageRef.child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@continueWithTask fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                val url = downloadUrl.toString()

                if (coverChecker == "cover") {
                    val mapCoverImg = HashMap<String, Any>()
                    mapCoverImg["cover"] = url
                    reference.updateChildren(mapCoverImg)
                    coverChecker = ""
                } else {
                    val mapProfileImg = HashMap<String, Any>()
                    mapProfileImg["profile"] = url
                    reference.updateChildren(mapProfileImg)
                    coverChecker = ""
                }

                progressBar.dismiss()
            }
        }.addOnFailureListener { error ->
            showToast("error : $error")
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }



}
























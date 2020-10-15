package com.masuwes.messagingapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.ui.Utils
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener(this)
        tv_register.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.tv_register -> finish()
            R.id.btn_register -> registerUser()
        }
    }

    private fun registerUser() {
        val username = edt_username_register.text.toString()
        val email = edt_email_register.text.toString()
        val password = edt_password_register.text.toString()

        when {
            username.isBlank() -> showToast("please enter your username")
            email.isBlank() -> showToast("please enter your email")
            password.isBlank() -> showToast("please enter your password")
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUserId = mAuth.currentUser!!.uid
                    reference = FirebaseDatabase.getInstance().reference.child(Utils.USER_TABLE).child(firebaseUserId)

                    val userHashMap = HashMap<String, Any> ()
                    userHashMap["uid"] = firebaseUserId
                    userHashMap["username"] = username
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/fir-cloudmessaging-41e26.appspot.com/o/ic_person_rectangle.png?alt=media&token=4b7a40de-a4a1-402d-b38f-34dbe7c6e023"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/fir-cloudmessaging-41e26.appspot.com/o/cover.png?alt=media&token=96b09046-55d1-449f-9dc6-bb8a9f67341a"
                    userHashMap["status"] = "not added yet"
                    userHashMap["search"] = username.toLowerCase()
                    userHashMap["facebook"] = "https://www.facebook.com/"
                    userHashMap["instagram"] = "https://www.instagram.com/"
                    userHashMap["website"] = "https://www.google.com/"

                    reference.updateChildren(userHashMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                startActivity(Intent(this, MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                                finish()
                            }
                        }

                } else {
                    showToast("Error message: ${task.exception?.message.toString()}")
                }
        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}














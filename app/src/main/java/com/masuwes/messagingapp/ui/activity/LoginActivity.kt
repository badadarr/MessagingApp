package com.masuwes.messagingapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.ui.showToast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_login.setOnClickListener(this)
        btn_login.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.tv_login -> finish()
            R.id.btn_login -> loginUser()
        }
    }

    private fun loginUser() {
        val email = edt_email_login.text.toString()
        val password = edt_password_login.text.toString()

        when {
            email.isBlank() -> showToast("please enter your email")
            password.isBlank() -> showToast("please enter your password")
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(
                        Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showToast("error message : ${task.exception?.message.toString()}")
                }
            }
    }

}

















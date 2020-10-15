package com.masuwes.messagingapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.masuwes.messagingapp.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btn_welcome_sign_in.setOnClickListener(this)
        btn_welcome_sign_up.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_welcome_sign_up -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            R.id.btn_welcome_sign_in -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}



















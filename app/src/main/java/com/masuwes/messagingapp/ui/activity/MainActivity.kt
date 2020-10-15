package com.masuwes.messagingapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.Users
import com.masuwes.messagingapp.ui.Utils
import com.masuwes.messagingapp.ui.adapter.SectionPagerAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""

        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager)

        view_pager.adapter = sectionPagerAdapter
        tabs.setupWithViewPager(view_pager)

        tv_username.setOnClickListener {
            startActivity(Intent(this, MessageChatActivity::class.java))
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().reference.child(Utils.USER_TABLE).child(firebaseUser.uid)

        // display username and profile pict
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(Users::class.java)
                    tv_username.text = users?.username
                    Picasso.get()
                        .load(users?.profile)
                        .placeholder(R.drawable.ic_person)
                        .into(profile_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                startActivity(
                    Intent(this, WelcomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()

                return true
            }
        }
        return false
    }
}
















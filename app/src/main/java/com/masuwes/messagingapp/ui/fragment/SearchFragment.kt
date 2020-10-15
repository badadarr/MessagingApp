package com.masuwes.messagingapp.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.Users
import com.masuwes.messagingapp.ui.Utils
import com.masuwes.messagingapp.ui.adapter.UserAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var mUser: ArrayList<Users>
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_search_user)
        editText = view.findViewById(R.id.edt_search_user)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        mUser = ArrayList()
        retrieveAllUser()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun retrieveAllUser() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child(Utils.USER_TABLE)
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser).clear()
                if (editText.text.toString() == "") {
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(Users::class.java)
                        if (!(user!!.uid).equals(firebaseUserId)) {
                            (mUser).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUser, false)
                    recyclerView.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun searchUsers(str: String) {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference
            .child(Utils.USER_TABLE).orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff") // diakhiri dengan tanda apple

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser).clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    if (!(user!!.uid).equals(firebaseUserId)) {
                        (mUser).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUser, false)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}















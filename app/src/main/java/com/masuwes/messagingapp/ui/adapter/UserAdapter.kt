package com.masuwes.messagingapp.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.Users
import com.masuwes.messagingapp.ui.Utils
import com.masuwes.messagingapp.ui.activity.MainActivity
import com.masuwes.messagingapp.ui.activity.MessageChatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_search_item.view.*

class UserAdapter(
    var context: Context,
    var users: List<Users>,
    var isChatCheck: Boolean
): RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_search_item, parent, false)
        )



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = users[position]
        holder.bind(data)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )

            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Choose action")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(context, MessageChatActivity::class.java)
                    intent.putExtra(Utils.PACKAGE, data.uid)
                    context.startActivity(intent)
                }
                else if (position == 1) {

                }
            })
            builder.show()
        }
    }

    override fun getItemCount(): Int = users.size

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(users: Users) {
            with(itemView) {
                friend_username.text = users.username
                Picasso.get().load(users.profile)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(friend_profile_pict)
            }
        }
    }




}
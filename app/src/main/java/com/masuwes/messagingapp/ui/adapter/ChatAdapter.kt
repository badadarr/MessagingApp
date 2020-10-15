package com.masuwes.messagingapp.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.masuwes.messagingapp.R
import com.masuwes.messagingapp.model.Chat
import com.masuwes.messagingapp.model.ChatList
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*
import kotlinx.android.synthetic.main.message_item_left.view.show_text_message
import kotlinx.android.synthetic.main.message_item_left.view.text_seen
import kotlinx.android.synthetic.main.message_item_right.view.*

class ChatAdapter(var context: Context, var chatList: List<Chat>, var imageUrl: String)
    : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]

        if(holder.profileImage != null) if(imageUrl.isNotBlank()) Picasso.get().load(imageUrl).into(holder.profileImage)

        /** image message - right side **/
        if (chat.message.equals("sent you an image") && !chat.url.isNullOrEmpty()) {
            // image message - right side
            if (chat.sender.equals(firebaseUser.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.rightImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.rightImageView)
            }

            /** image message - left side **/
            else if (!chat.sender.equals(firebaseUser.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.leftImageView.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.leftImageView)
            }
        } else {
            holder.showTextMessage?.text = chat.message
        }

        // sent and seen message
        if (position == chatList.size-1) {

            if (chat.isseen!!) {
                holder.textSeen?.text = context.resources.getString(R.string.seen)

                if (chat.message == "sent you an image" && !chat.url.isNullOrEmpty()) {
                    val lp = holder.textSeen?.layoutParams as ConstraintLayout.LayoutParams
                    lp.setMargins(0, 245, 10, 0)
                    holder.textSeen.layoutParams = lp
                }
            } else {
                holder.textSeen?.text = context.resources.getString(R.string.sent)

                if (chat.message == "sent you an image" && !chat.url.isNullOrEmpty()) {
                    val lp = holder.textSeen?.layoutParams as ConstraintLayout.LayoutParams
                    lp.setMargins(0, 245, 10, 0)
                    holder.textSeen.layoutParams = lp
                }
            }
        } else {
            holder.textSeen?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender.equals(firebaseUser.uid)) 1 else 0
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var profileImage = itemView.friend_profile_left
        var showTextMessage = itemView.show_text_message
        var textSeen = itemView.text_seen
        var leftImageView = itemView.image_view_left
        var rightImageView = itemView.image_view_right

    }

}
















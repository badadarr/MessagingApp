package com.masuwes.messagingapp.ui

import android.content.Context
import android.widget.Toast

object Utils {
    const val REQUEST_CODE = 438
    const val PACKAGE = "visit_id"
    const val USER_TABLE = "Users"
    const val USER_IMAGE_TABLE = "User Images"
    const val CHATS_TABLE = "Chats"
    const val CHAT_IMAGE_TABLE = "Chat Images"
    const val CHAT_LIST = "ChatList"
    const val TOKENS_TABLE = "Tokens"
}

fun Context.showToast(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_LONG
    ).show()
}
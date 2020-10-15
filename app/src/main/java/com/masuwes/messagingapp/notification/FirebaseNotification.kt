package com.masuwes.messagingapp.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.masuwes.messagingapp.ui.activity.MainActivity

class FirebaseNotification : FirebaseMessagingService() {

    @SuppressLint("ObsoleteSdkInt")
    override fun onMessageReceived(mRemoteMessage: RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)

        val sented = mRemoteMessage.data["sented"]
        val user = mRemoteMessage.data["user"]
        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser = sharedPref.getString("currentUser", "none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented == firebaseUser.uid) {
            if (currentOnlineUser != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(mRemoteMessage)
                } else {
                    sendNotification(mRemoteMessage)
                }
            }
        }

    }

    private fun sendNotification(mRemoteMessage: RemoteMessage) {
        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification = mRemoteMessage.notification
        val regex = user?.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, regex, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    private fun sendOreoNotification(mRemoteMessage: RemoteMessage) {

    }
}



















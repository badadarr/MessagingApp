package com.masuwes.messagingapp.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAFfBlr60:APA91bEdvO4Hla5xZlXyIrZOTJse9M7yzi1uM48024-l3JBr2nrd9dw9bJzBh1snmObtk4gZbaRN-A75R2RiaRHfLVRnFtCpQfsIKOSZdw9NjgmfAuZKjj_7v1xg-5k8dR01lPP8CSmJ"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender) : Call<Response>
}
package com.masuwes.messagingapp.notification

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {

    object Client : ApiService {
        private lateinit var retrofit: Retrofit

        fun getClient(url: String): Retrofit {
            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit
        }

        override fun sendNotification(body: Sender): Call<Response> {
            TODO("Not yet implemented")
        }


    }
}
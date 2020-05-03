package com.creativeoffice.cobansut.Services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService (){

    override fun onMessageReceived(p0: RemoteMessage) {

        var bildirimBaslik = p0!!.notification!!.title
        var bildirimBody = p0!!.notification!!.body
        var bildirimData = p0!!.data

        Log.e("FCM", "BAŞLIK: $bildirimBaslik  gövde: $bildirimBody data: $bildirimData")
        super.onMessageReceived(p0)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}
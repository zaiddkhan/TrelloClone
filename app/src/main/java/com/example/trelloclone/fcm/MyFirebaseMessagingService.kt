package com.example.trelloclone.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.trelloclone.MainActivity
import com.example.trelloclone.R
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.logins.SignInActivity
import com.example.trelloclone.utils.Constants
import com.google.android.gms.common.SignInButton
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.isNotEmpty().let {

            val title=remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!


            sendNotification(title,message)
        }
        remoteMessage.notification?.let {

        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?){

    }
    private fun sendNotification(title:String,messageBody:String){
        val intent = if(FirestoreClass().getCurrentUserId().isNotEmpty()){
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
       val channelId = resources.getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Title")
            .setContentText("Message")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
                ) as NotificationManager


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"KAAM KARO CHANNEL",NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)

        }

        notificationManager.notify(0,notificationBuilder.build())


    }
}
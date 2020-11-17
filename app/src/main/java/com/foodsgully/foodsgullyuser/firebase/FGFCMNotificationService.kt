package com.foodsgully.foodsgullyuser.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject

class FGFCMNotificationService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                task.result?.token?.let { storeNewToken(it) }
            })
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        storeNewToken(token)
    }


    private fun storeNewToken(token: String) {
        SharedPreferenceManager(this, FoodsGullyConstants.FCM_SP).storeStringPreference(
            FoodsGullyConstants.FIREBASE_TOKEN,
            token
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val messageJson = JSONObject(Gson().toJson(message.data))

        when(messageJson["messageType"] as String) {
            "ORDER_PLACED" -> {
                createNotification(messageJson["message"] as String)
            }

            else -> {
                createNotification(messageJson["message"] as String)
            }
        }


    }


    private fun createNotification(message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        intent.putExtra(FoodsGullyConstants.ARG_IS_ORDER_PLACED_NOTIFICATION,true)
        val notificationCount = FoodsGullyUtils.getNotificationCount(context = this) + 1

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this,FoodsGullyConstants.PRIMARY_CHANNEL)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
            notificationBuilder.color = resources.getColor(R.color.colorPrimary)
        } else notificationBuilder.setSmallIcon(R.drawable.ic_notification)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationCount, notificationBuilder.build())
        FoodsGullyUtils.setNotificationCount(notificationCount = notificationCount,context = this)
    }

}
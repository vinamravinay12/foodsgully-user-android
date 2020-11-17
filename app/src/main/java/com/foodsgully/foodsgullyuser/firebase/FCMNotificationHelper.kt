package com.foodsgully.foodsgullyuser.firebase

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.media.RingtoneManager
import android.os.Build
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants

class FCMNotificationHelper(private val ctx: Context?) : ContextWrapper(ctx) {
    private val manager: NotificationManager? = null
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(FoodsGullyConstants.PRIMARY_CHANNEL, name, importance)
            channel.description = description
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            createNotificationChannelGroup()

            ctx?.let { SharedPreferenceManager(it,FoodsGullyConstants.LOGIN_SP).storeBooleanPreference(FoodsGullyConstants.IS_CHANNEL_PREPARED,true) }
        }
    }

    private fun createNotificationChannelGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val groupId: String = FoodsGullyConstants.NOTIFICATION_CHANNEL_GROUP_ID
            // The user-visible name of the group.
            val groupName: CharSequence = getString(R.string.channel_group_name)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannelGroup(NotificationChannelGroup(groupId, groupName))
        }
    }

    // Class constructor
    init {
        createNotificationChannel()
    }
}
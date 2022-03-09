package com.info.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.info.adapter.TipsAdapter
import com.info.commons.Constants.STOCK_CHANGE_PAYLOAD_IDENTIFIER
import com.info.commons.DateTimeHelper
import com.info.tradewyse.DashBoardActivity
import com.info.tradewyse.R
import java.util.*


/**
 * Centralized area for making notifications that also allows debugging help.
 */
class NotificationProvider {

    companion object {
        const val STOCK_UPDATED_CHANNEL_NAME = "Tip Updates"
        const val STOCK_UPDATED_CHANNEL_ID = "tip-updates"
    }

    fun createChangedStocksNotification(context: Context, list: List<String>, title:String, description:String) {
        val displayList = list.joinToString(", ")
        val pendingIntent = PendingIntent.getActivity(context, 12,
            Intent(context, DashBoardActivity::class.java).apply {
                putExtra(STOCK_CHANGE_PAYLOAD_IDENTIFIER, true)
                setAction(java.lang.Long.toString(System.currentTimeMillis()))
            }, 0
        )
        val builder = NotificationCompat.Builder(context, "TradeTips")
            //.setContentTitle("Trade Tips - Watchlist Update ${DateTimeHelper.formatTime(Date())}")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.splash_logo)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.splash_logo))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setChannelId(STOCK_UPDATED_CHANNEL_ID)
        makeChannel(context, STOCK_UPDATED_CHANNEL_NAME, STOCK_UPDATED_CHANNEL_ID)
        with(NotificationManagerCompat.from(context)) {
            notify(1087654, builder.build())
        }

    }

    fun removeChangedStocksNotification(context: Context) {
        val notificationmanager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationmanager.cancel(1087654)
    }

    private fun makeChannel(context: Context, channelName: String, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
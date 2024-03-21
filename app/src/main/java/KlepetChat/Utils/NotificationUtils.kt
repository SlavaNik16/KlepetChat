package KlepetChat.Utils

import KlepetChat.Activities.Data.Constants
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.klepetchat.R


class NotificationUtils {

    private var instance: NotificationUtils? = null

    private var context: Context? = null
    private var manager: NotificationManager? = null

    private var lastId = 0

    private var notifications: HashMap<Int, Notification>? = null

    constructor(){

    }
    private constructor(context: Context?) {
        this.context = context
        manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifications = HashMap<Int, Notification>()
    }
    
    fun registerNotification(CHANNEL_NAME:String? = "test_channel_name"){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Constants.CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            manager?.createNotificationChannel(channel)
        }
    }
    fun sendNotificationCreate(title:String, text:String, pendingIntent:PendingIntent? = null):Int{
        var context = context!!
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_active)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if(pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return -1
        }
        var notification = builder.build()
        manager?.notify(lastId, notification)
        notifications?.put(lastId, notification)
        return lastId++
    }

    fun getInstance(context: Context?): NotificationUtils? {
        if (instance == null) {
            instance = NotificationUtils(context)
        } else {
            instance!!.context = context
        }
        return instance
    }
}
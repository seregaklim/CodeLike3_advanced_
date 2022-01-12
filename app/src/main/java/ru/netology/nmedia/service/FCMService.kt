
package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.R
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.PushToken
import kotlin.random.Random



class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleRecipientId(content: RecipientId) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.recipientId,
                    content.content,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }


    override fun onNewToken(token: String) {
        println(token)
        AppAuth.getInstance().sendPushToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val authState = AuthState ()

        val recipientId = message.data["recipientId"] // тут получаем ид для кого пуш
        if(recipientId==authState.token ||recipientId == null )

            message.data[action]?.let { when (Action.valueOf(it)) {
                Action.RECIPIENTID-> handleRecipientId(gson.fromJson(
                    message.data[content], RecipientId::class.java
                )
                )
            }
                if ( recipientId == null  &&  recipientId != authState.token  || recipientId !=null &&  recipientId !=authState.token)

                    CoroutineScope(Dispatchers.Default).launch {
                        try {
                            val pushToken = PushToken(authState.token ?: Firebase.messaging.token.await())
                            Api.service.save(pushToken)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            }
//        message.data[action]?.let {
//            when (Action.valueOf(it)) {
//                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
//            }
//        }

    }
}


enum class Action {
    LIKE,
    RECIPIENTID
}

data class RecipientId(
    val recipientId :String,
    val content: String
)

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)




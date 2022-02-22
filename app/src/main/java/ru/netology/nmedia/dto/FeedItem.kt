package ru.netology.nmedia.dto

import android.annotation.SuppressLint
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import ru.netology.nmedia.enumeration.AttachmentType
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDate
import java.time.Month
import java.util.*

sealed class FeedItem{

abstract val timing:Long
    abstract val id: Long
}
//классами для рекламы
data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
  override  val timing: Long=data()
    ) : FeedItem()

data class Timing (
    override val id: Long,
 override   val timing: Long=data()

    ) : FeedItem()


data class Post
(
    override val id: Long,
    val author:String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    val likes: Int = 0,
    val newer: Long,
    val authorId: Long,
    var ownedByMe: Boolean = false,
    override    val timing: Long=data(),
    val attachment: Attachment? = null,

    ): FeedItem()


data class Attachment(
    val url: String,
    val type: AttachmentType,
)

fun  data(): Long {
    val date=Date();
    val  time:Long  = date.getTime();
    return time
}


package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType
import java.sql.Date
import java.sql.Time
import java.time.LocalDateTime

sealed class FeedItem{


    abstract val id: Long
}
//классами для рекламы
data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
     val timing :Timing
    ) : FeedItem()

data class Timing (
    override val id: Long,
    val  timing: String

) : FeedItem()


data class Post(
    override  val id: Long,
    val author :String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    val likes: Int = 0,
    val newer: Long,
    val  authorId: Long,
    val attachment: Attachment? = null,
    var ownedByMe: Boolean = false
): FeedItem()

data class Attachment(
    val url: String,
    val type: AttachmentType,
)



package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType
import java.util.*
sealed class FeedItem{

    abstract val id: Long
}
//классами для рекламы
data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
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
    val timing: Long=data(),
    val attachment: Attachment? = null,

    ): FeedItem()

////метод времени
fun  data(): Long {
    val date=Date();
    val  time:Long  = date.getTime();
    return time
}




data class Attachment(
    val url: String,
    val type: AttachmentType,
)
package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe:Boolean,
    val likes: Int,
    val authorAvatar: String,
    val newer:Long,
    val attachment: Attachment? = null
    )

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

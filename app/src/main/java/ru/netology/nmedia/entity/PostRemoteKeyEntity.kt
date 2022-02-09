package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
//отвечать за ключи последнего полученного элемента (сверху и снизу).
@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Long,
) {
    enum class KeyType {
        AFTER, BEFORE
    }
}
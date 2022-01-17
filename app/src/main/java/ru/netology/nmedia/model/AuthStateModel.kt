package ru.netology.nmedia.model

import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Post

data class AuthStateModel (
    val authState: List<AuthState> = emptyList(),
    val empty: Boolean = false,
)



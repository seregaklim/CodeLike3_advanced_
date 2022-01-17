package ru.netology.nmedia.model

data class ErrorModel(val type: ErrorType, val action: ActionType, val message: String = "", )

sealed class ActionType {
    object GetAll : ActionType()
    object Save : ActionType()
    object RemoveById: ActionType()

    object Like:ActionType()
    object unlikeById : ActionType()
    object Refresh : ActionType()
    object CountMessegePost: ActionType()
    object UnCountMessegePost: ActionType()
    object UpdateUser:ActionType()
    object RegisterUser: ActionType()
    object GetUserId: ActionType()
}

sealed class ErrorType {
    object NetworkError : ErrorType()
    object AppError : ErrorType()
    object ServerError : ErrorType()
}
package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.http.Field
import ru.netology.nmedia.auth.AppAuth.Companion.getInstance
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.model.ErrorModel
import ru.netology.nmedia.model.ErrorType
import ru.netology.nmedia.util.SingleLiveEvent

private val  authState  = AuthState(
    id = 0,
    token = null)


@ExperimentalCoroutinesApi
class AuthViewModel : ViewModel() {
    val data: LiveData<AuthState> = getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = getInstance().authStateFlow.value.id != 0L

    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error


    fun updateUser(@Field("login") login: String, @Field("pass") pass: String) {
        try {

        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.UpdateUser, e.message ?: ""
                )
            )

        }

    }

    fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ) {
        try {

        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.RegisterUser, e.message ?: ""
                )
            )

        }
    }

    fun getUserId(authState: AuthState) {


        try {
            authState.token?.let {
                 authState.copy(authState.token?.toLong())
                authState.id?.let {
                    authState.copy(authState.id)

                }
            }
        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.RegisterUser, e.message ?: ""
                )
            )

        }

    }
}


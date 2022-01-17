package ru.netology.nmedia.viewmodel

import android.app.Application
import android.support.v4.app.INotificationSideChannel
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AppAuth.Companion.getInstance
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.AuthRepository
import ru.netology.nmedia.repository.AuthRepositoryimpl
import ru.netology.nmedia.util.SingleLiveEvent

private val  authState  = AuthState(
    id = 0,
    token = null)

@ExperimentalCoroutinesApi
class AuthViewModel(application: Application) : AndroidViewModel(application){

    private val repository: AuthRepository =
        AuthRepositoryimpl(AppDb.getInstance(context = application).authStateDao(),
            AppDb.getInstance(context = application).userDao())


        val data: LiveData<AuthState> = getInstance()
            .authStateFlow
            .asLiveData(Dispatchers.Default)

        val authenticated: Boolean
            get() = getInstance().authStateFlow.value.id != 0L


    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun updateUser(login:String, pass:String) = viewModelScope.launch {
         try {
             repository.updateUser(login,pass)
         } catch (e: Exception) {
             _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
             //_data.postValue(FeedModel(error = true))
         }
     }

      fun registerUser(login: String,
                              pass: String,
                              name: String) = viewModelScope.launch {
          try {
              repository.registerUser(login,pass,name)
          } catch (e: Exception) {
              _error.postValue(ErrorModel(ErrorType.AppError, ActionType.RegisterUser, e.message ?: ""))
              //_data.postValue(FeedModel(error = true))
          }
      }



//    fun getUserId(id:Long, token:String)= viewModelScope.launch {
//        try {
//            repository.getUserId(id,token)
//        } catch (e: Exception) {
//            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.GetUserId, e.message ?: ""))
//            //_data.postValue(FeedModel(error = true))
//        }
//    }
}


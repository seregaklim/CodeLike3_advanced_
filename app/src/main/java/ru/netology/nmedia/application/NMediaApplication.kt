package ru.netology.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject
//Первая аннотация – @HiltAndroidApp, которую мы должны разместить над классом нашего приложения:
@HiltAndroidApp
class NMediaApplication : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    //Hilt предлагает обеспечить внедрение зависимостей в поля с помощью комбинации аннотаций
    // @AndroidEntryPoint(ставится над классом) и @Inject (ставится над полем)
    // Важно: поля должны быть lateinit var и не private!
    //Поддерживаются только следующие классы:●Activity;●Fragment;●Service;●BroadcastReceiver;●View.
    @Inject
    lateinit var auth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
    }

    private fun setupAuth() {
        appScope.launch {
            auth.sendPushToken()
        }
    }
}


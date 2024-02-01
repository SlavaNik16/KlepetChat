package KlepetChat.Hilts

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Application : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
package KlepetChat

import KlepetChat.Activities.ActivitiesCallbacks.MyActivityLifecycleCallbacks
import android.app.Application
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EmojiManager.install(GoogleEmojiProvider())
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }

}
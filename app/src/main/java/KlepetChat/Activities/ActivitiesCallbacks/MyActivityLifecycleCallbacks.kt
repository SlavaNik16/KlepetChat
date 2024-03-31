package KlepetChat.Activities.ActivitiesCallbacks

import KlepetChat.Activities.AuthorizationActivity
import KlepetChat.Activities.IntroActivity
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Enums.StatusTypes
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MyActivityLifecycleCallbacks : ActivityLifecycleCallbacks {

    private var countActivity = 0
    var userViewModel: UserViewModel? = null
    private var status: StatusTypes = StatusTypes.Offline
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.e("Activity", "onActivityCreated:" + activity.getLocalClassName());
        if (activity is AuthorizationActivity && status == StatusTypes.Online) {
            status = StatusTypes.Offline
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Log.e("Activity", "onActivityStarted:" + activity.getLocalClassName());
        ++countActivity
        if (activity is AppCompatActivity && activity !is IntroActivity) {
            userViewModel = ViewModelProvider(activity)[UserViewModel::class.java]
            isEnter()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Log.e("Activity", "onActivityResumed:" + activity.getLocalClassName());
    }

    override fun onActivityPaused(activity: Activity) {
        Log.e("Activity", "onActivityPaused:" + activity.getLocalClassName());

    }

    override fun onActivityStopped(activity: Activity) {
        Log.e("Activity", "onActivityStopped:" + activity.getLocalClassName());
        --countActivity
        if (activity is AppCompatActivity && activity !is IntroActivity) {
            userViewModel = ViewModelProvider(activity)[UserViewModel::class.java]
            isExit()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.e("Activity", "onActivitySaveInstanceState:" + activity.getLocalClassName());
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.e("Activity", "onActivityDestroyed:" + activity.getLocalClassName());
    }

    private fun isExit() {
        Log.e("Activity", "Кол-во activity: " + countActivity);
        if (countActivity <= 0) {
            Log.e("Activity", "Пользователь вышел: " + countActivity);
            putStatusOffline()
        }
    }

    private fun isEnter() {
        Log.e("Activity", "Кол-во activity: " + countActivity);
        if (status == StatusTypes.Offline) {
            Log.e("Activity", "Пользователь зашел: " + countActivity);
            putStatusOnline()
        }
    }

    private fun putStatusOffline() {
        userViewModel?.putStatus(
            StatusTypes.Offline,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Log.d("Activity", "Не поменял статус Offline")
                }
            })
        status = StatusTypes.Offline
    }

    private fun putStatusOnline() {
        userViewModel?.putStatus(
            StatusTypes.Online,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Log.d("Activity", "Не поменял статус Online")
                }
            })
        status = StatusTypes.Online
    }

}

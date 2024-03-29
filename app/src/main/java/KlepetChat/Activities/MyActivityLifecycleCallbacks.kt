package KlepetChat.Activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log

class MyActivityLifecycleCallbacks : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.e("Activity","onActivityCreated:" + activity.getLocalClassName());
    }

    override fun onActivityStarted(activity: Activity) {
        Log.e("Activity","onActivityStarted:" + activity.getLocalClassName());
    }

    override fun onActivityResumed(activity: Activity) {
        Log.e("Activity","onActivityResumed:" + activity.getLocalClassName());
    }

    override fun onActivityPaused(activity: Activity) {
        Log.e("Activity","onActivityPaused:" + activity.getLocalClassName());

    }

    override fun onActivityStopped(activity: Activity) {
        Log.e("Activity","onActivityStopped:" + activity.getLocalClassName());
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.e("Activity","onActivitySaveInstanceState:" + activity.getLocalClassName());
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.e("Activity","onActivityDestroyed:" + activity.getLocalClassName());
    }

}
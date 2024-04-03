package KlepetChat.Activities

import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val userDataViewModel: UserDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setObserve()
    }
    private fun setObserve(){
        userDataViewModel.userData.observe(this) { validateToken(it) }
    }
    private fun validateToken(userData: UserData?) {
        if (!userData?.accessToken.isNullOrBlank()) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (userData!!.isFirst) {
            var intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            var intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }
}
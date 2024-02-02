package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthRepository @Inject constructor(
    private val authService: IAuthService
):AuthViewModel() {
    fun Login(login: Login) {
        CoroutineScope(Dispatchers.Unconfined).launch {
            var response = authService.postLogin(login.phone, login.password)
            if(response.isSuccessful) {
                response.body()?.let { data ->
                    LoginResponse.value = data
                }
            }

        }

    }
}

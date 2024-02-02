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
        CoroutineScope(Dispatchers.IO).launch {
            var response = authService.postLogin(login)
            if (response.isExecuted) {
                ErrorResponse.value =
                    KlepetChat.WebApi.Models.Exceptions.Error(400, "Плохая авторизация");
                return@launch
            }
            response.enqueue(object : Callback<Token> {
                override fun onFailure(call: Call<Token>, t: Throwable) {
                    ErrorResponse.value =
                        KlepetChat.WebApi.Models.Exceptions.Error(400, "Плохая авторизация");
                }

                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful) {
                        LoginResponse.value = response.body();
                    }
                }
            })

        }
    }
}
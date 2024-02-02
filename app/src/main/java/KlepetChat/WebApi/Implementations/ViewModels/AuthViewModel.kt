package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.Repositories.AuthRepository
import KlepetChat.WebApi.Models.Exceptions.Error
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

open class AuthViewModel :ViewModel() {
    fun getLoginObserve() = LoginResponse
    fun getErrorObserve() = ErrorResponse

    val LoginResponse: MutableLiveData<Token?> by lazy {
        MutableLiveData<Token?>()
    }

    val ErrorResponse: MutableLiveData<Error> by lazy {
        MutableLiveData<Error>()
    }
}
package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.AuthRepository

import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): BaseViewModel() {
    private val tokenResponse = MutableLiveData<ApiResponse<Token>>()
    fun GetToken() : MutableLiveData<ApiResponse<Token>>
    {
        return tokenResponse
    }
    fun login(login: Login, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        GetToken(),
        coroutineErrorHandler
    ){
        authRepository.login(login)
    }

//    fun login(login: Login){
//        authRepository.login(login)
//    }
}

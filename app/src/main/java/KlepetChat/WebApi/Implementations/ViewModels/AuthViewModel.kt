package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.AuthRepository

import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): BaseViewModel() {
    private val tokenResponse = MutableLiveData<ApiResponse<Token>>()
    val token = tokenResponse
    fun login(login: Login, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        tokenResponse,
        coroutineErrorHandler
    ){
        authRepository.login(login)
    }

    fun loginTest(login: Login, coroutineErrorHandler: ICoroutinesErrorHandler){
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(login).collect() {
                withContext(Dispatchers.Main) {
                    tokenResponse.value = it
                }
            }
        }
    }

}

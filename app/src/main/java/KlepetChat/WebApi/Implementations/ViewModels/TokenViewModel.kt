package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.TokenRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class TokenViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
): BaseViewModel() {
    private val tokenResponseBoolean = MutableLiveData<ApiResponse<Boolean>>()

    fun postCreate(phone:String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        tokenResponseBoolean,
        coroutineErrorHandler
    ){
        tokenRepository.postCreate(phone)
    }




}
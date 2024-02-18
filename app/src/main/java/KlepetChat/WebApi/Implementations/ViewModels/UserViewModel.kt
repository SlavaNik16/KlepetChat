package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.UserRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Token
import KlepetChat.WebApi.Models.Response.User
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private  val userRepository: UserRepository
):BaseViewModel() {
    private val userResponse = MutableLiveData<ApiResponse<User>>()
    val user = userResponse
    fun getByPhone(phone:String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        user,
        coroutineErrorHandler
    ){
        userRepository.getByPhone(phone)
    }


}
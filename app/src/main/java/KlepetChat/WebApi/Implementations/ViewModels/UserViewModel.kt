package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.UserRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.User
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
):BaseViewModel() {
    private val userResponse = MutableLiveData<ApiResponse<User>>()
    private val usersResponse = MutableLiveData<ApiResponse<MutableList<User>>>()
    val user = userResponse
    val users = usersResponse
    fun getByPhone(phone:String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ){
        userRepository.getByPhone(phone)
    }

    fun getContactsOther( coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        usersResponse,
        coroutineErrorHandler
    ){
        userRepository.getContactsOther()
    }

    fun postCreate(userRegister:UserRegister, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ){
        userRepository.postCreate(userRegister)
    }


}
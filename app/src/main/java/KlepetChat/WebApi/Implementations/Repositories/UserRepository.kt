package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IUserService
import KlepetChat.WebApi.Models.Request.UserRegister
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: IUserService
) {
    fun getByPhone(phone:String) = ApiRequestFlowResponse {
        userService.getByPhone(phone)
    }
    fun postCreate(userRegister:UserRegister) = ApiRequestFlowResponse {
        userService.postCreate(userRegister)
    }
}
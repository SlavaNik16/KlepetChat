package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.User.IUserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: IUserService
) {
    fun getByPhone(phone:String) = ApiRequestFlowResponse {
        userService.getByPhone(phone)
    }
}
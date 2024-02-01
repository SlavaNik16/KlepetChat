package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlow
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Models.Request.Login
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: IAuthService,
) {
    fun login(login: Login) = ApiRequestFlow {
        authApiService.postLogin(login)
    }
}
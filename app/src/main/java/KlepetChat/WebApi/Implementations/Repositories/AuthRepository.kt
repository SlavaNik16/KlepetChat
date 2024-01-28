package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlow
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Models.Request.Auth
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: IAuthService,
) {
    fun login(auth: Auth) = ApiRequestFlow {
        authService.postLogin(auth)
    }
}
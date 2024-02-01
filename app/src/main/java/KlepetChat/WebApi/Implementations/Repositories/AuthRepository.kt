package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlow
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Models.Request.Auth
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: IAuthService,
) {
    fun login(auth: Auth) = ApiRequestFlow {
        authApiService.postLogin(auth)
    }
}
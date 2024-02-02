package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Models.Request.Login
import javax.inject.Inject

class AuthRepositoryTest @Inject constructor(
    private val authApiService: IAuthService,
) {
    fun login(login: Login) = ApiRequestFlowResponse {
        authApiService.postLogin(login.phone, login.password)
    }

}
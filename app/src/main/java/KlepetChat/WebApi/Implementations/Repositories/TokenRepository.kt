package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.ITokenService
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val tokenService: ITokenService,
) {

    fun postCreate(phone: String) = ApiRequestFlowResponse {
        tokenService.postCreate(phone)
    }

    fun deleteToken() = ApiRequestFlowResponse {
        tokenService.deleteToken()
    }

}
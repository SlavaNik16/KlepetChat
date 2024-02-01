package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.Auth
import KlepetChat.WebApi.Models.Response.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ITokenService {
    @POST("token/refresh")
    suspend fun postRefresh(@Body token: Token): Response<Token>
}
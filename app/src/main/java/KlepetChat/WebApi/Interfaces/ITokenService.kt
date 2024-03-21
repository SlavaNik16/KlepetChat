package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Response.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ITokenService {

    @POST("token")
    suspend fun postCreate(@Query("phone") phone: String): Response<Boolean>

    @POST("token/refresh")
    suspend fun postRefresh(@Body token: Token): Response<Token>
}
package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthService {
    @POST("auth/login")
    suspend fun postLogin(@Body() login:Login): Response<Token>
}
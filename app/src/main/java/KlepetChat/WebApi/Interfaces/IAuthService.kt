package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface IAuthService {
    @POST("auth/login")
    suspend fun postLogin(@Body() login:Login): Response<Token>
}
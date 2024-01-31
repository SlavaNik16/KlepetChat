package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.Auth
import KlepetChat.WebApi.Models.Response.Token
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface IAuthService:IApiService {
    @POST("auth/login")
    suspend fun postLogin(@Body auth: Auth): Response<Token>
}
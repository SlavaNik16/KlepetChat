package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import android.app.Notification.Action
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface IAuthService {
    @POST("auth/login")
    suspend fun postLogin(@Body login: Login): Response<Token>
}
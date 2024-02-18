package KlepetChat.WebApi.Interfaces.User

import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import KlepetChat.WebApi.Models.Response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface IUserService {
    @POST("User/phone")
    suspend fun getByPhone(@Query("phone") phone:String): Response<User>
}
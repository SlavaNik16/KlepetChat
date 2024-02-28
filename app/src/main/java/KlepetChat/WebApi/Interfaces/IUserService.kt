package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IUserService {
    @GET("user/{phone}")
    suspend fun getByPhone(@Path("phone") phone:String): Response<User>

    @GET("user/contacts")
    suspend fun getContactsOther():Response<MutableList<User>>

    @POST("user")
    suspend fun postCreate(@Body uerRegister:UserRegister): Response<User>
}
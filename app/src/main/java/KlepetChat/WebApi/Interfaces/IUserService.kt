package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.FIO
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface IUserService {
    @GET("user")
    suspend fun validateUser(@Query("password") password: String): Response<ResponseBody>

    @GET("user/{phone}")
    suspend fun getByPhone(@Path("phone") phone: String): Response<User>

    @GET("user/search/{name}")
    suspend fun getUsersByName(@Path("name") name: String): Response<MutableList<User>>

    @GET("user/contacts")
    suspend fun getContactsOther(): Response<MutableList<User>>

    @GET("user/all/{chatId}")
    suspend fun getAllUserByChatId(@Path("chatId") chatId: UUID): Response<MutableList<User>>

    @PUT("user/fio")
    suspend fun putFIO(@Body fio: FIO): Response<User>

    @PUT("user/aboutMe")
    suspend fun putAboutMe(@Query("aboutMe") aboutMe: String): Response<User>

    @PUT("user/nickname")
    suspend fun putNickname(@Query("nickname") nickname: String): Response<User>

    @PUT("user/photo")
    suspend fun putPhoto(@Query("photo") photo: String): Response<User>

    @PUT("user/phone")
    suspend fun putPhone(@Body login: Login): Response<User>

    @POST("user")
    suspend fun postCreate(@Body uerRegister: UserRegister): Response<User>


}
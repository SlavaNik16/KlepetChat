package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Response.Chat
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface IChatService {

    @GET("chat")
    suspend fun getChats(): Response<MutableList<Chat>>

    @GET("chat/get/{id}")
    suspend fun getChatById(@Path("id") id: UUID): Response<Chat>

    @GET("chat/{name}")
    suspend fun getChatsByName(@Path("name") name: String): Response<MutableList<Chat>>

    @GET("chat/get/contact/{phoneOther}")
    suspend fun getChatByPhone(@Path("phoneOther") phoneOther: String): Response<Chat>

    @POST("chat/favorites")
    suspend fun postFavorites(@Query("userId") userId: UUID): Response<Boolean>

    @POST("chat/contact")
    suspend fun postContact(@Query("phoneOther") phoneOther: String): Response<Chat>

    @POST("chat/group")
    suspend fun postGroup(
        @Query("name") name: String,
        @Query("photo") photo: String? = null,
    ): Response<Chat>

    @POST("chat/join/{id}")
    suspend fun postJoinGroup(@Path("id") id: UUID): Response<Chat>

    @POST("chat/leave/{id}")
    suspend fun postLeaveGroup(@Path("id") id: UUID): Response<Boolean>

    @PUT("chat/photo")
    suspend fun putEditPhoto(
        @Query("id") id: UUID,
        @Query("photo") photo: String?,
    ): Response<Chat>

    @DELETE("chat/{id}")
    suspend fun deleteChat(@Path("id") id: UUID): Response<ResponseBody>

}
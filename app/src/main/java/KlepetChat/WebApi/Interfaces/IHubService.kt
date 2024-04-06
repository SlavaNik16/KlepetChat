package KlepetChat.WebApi.Interfaces

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface IHubService {
    @POST("ChatHub/JoinGroup/{connectionId}/{groupName}")
    suspend fun joinGroup(
        @Path("connectionId") connectionId: String,
        @Path("groupName") groupName: String,
    ): Response<ResponseBody>

    @POST("ChatHub/LeaveGroup/{connectionId}/{groupName}")
    suspend fun leaveGroup(
        @Path("connectionId") connectionId: String,
        @Path("groupName") groupName: String,
    ): Response<ResponseBody>

    @POST("ChatHub/SendMessage")
    suspend fun sendMessage(
        @Query("chatId") chatId: UUID, @Query("message") message: String,
        @Query("groupName") groupName: String,
    ): Response<ResponseBody>

    @POST("ChatHub/PrintGroup/{groupName}")
    suspend fun printGroup(
        @Path("groupName") groupName: String,
        @Query("isStart") isStart: Boolean,
    ): Response<ResponseBody>


    @POST("ChatHub/SendRegister/{connectionId}")
    suspend fun sendRegister(@Path("connectionId") connectionId: String): Response<ResponseBody>

    @POST("ChatHub/SendNotificationContact")
    suspend fun sendNotificationContact(
        @Query("phoneOther") phoneOther: String,
        @Query("chatId") chatId: UUID,
        @Query("message") message: String,
    ): Response<ResponseBody>

    @POST("ChatHub/SendNotificationGroup")
    suspend fun sendNotificationGroup(
        @Query("phoneOther") phoneOther: String,
        @Query("chatId") chatId: UUID,
        @Query("messageId") messageId: UUID,
    ): Response<ResponseBody>

    @POST("ChatHub/UpdateChat")
    suspend fun updateChat(
        @Query("phoneOther") phoneOther: String,
    ): Response<ResponseBody>

    @POST("ChatHub/UpdateChatInfo")
    suspend fun updateChatInfo(
        @Query("phoneOther") phoneOther: String,
    ): Response<ResponseBody>

    @POST("ChatHub/UpdateMessageContact")
    suspend fun updateMessageContact(
        @Query("phoneOther") phoneOther: String,
    ): Response<ResponseBody>

    @POST("ChatHub/UpdateMessageGroup")
    suspend fun updateMessageGroup(
        @Query("phoneOther") phoneOther: String
    ): Response<ResponseBody>

    @POST("ChatHub/ExitChat")
    suspend fun exitChat(
        @Query("phoneOther") phoneOther: String,
    ): Response<ResponseBody>

}
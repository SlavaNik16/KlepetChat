package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IChatService
import java.util.UUID
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val chatService: IChatService,
) {
    fun getChats() = ApiRequestFlowResponse {
        chatService.getChats()
    }

    fun getChatsByName(name: String) = ApiRequestFlowResponse {
        chatService.getChatsByName(name)
    }

    fun postFavorites(userId: UUID) = ApiRequestFlowResponse {
        chatService.postFavorites(userId)
    }

    fun postContact(phoneOther: String) = ApiRequestFlowResponse {
        chatService.postContact(phoneOther)
    }

    fun postGroup(name: String, photo: String? = null) = ApiRequestFlowResponse {
        chatService.postGroup(name, photo)
    }

    fun postJoinGroup(id: UUID) = ApiRequestFlowResponse {
        chatService.postJoinGroup(id)
    }
    fun deleteChat(id: UUID) = ApiRequestFlowResponse {
        chatService.deleteChat(id)
    }

}
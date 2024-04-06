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

    fun getChatById(id: UUID) = ApiRequestFlowResponse {
        chatService.getChatById(id)
    }

    fun getChatsByName(name: String) = ApiRequestFlowResponse {
        chatService.getChatsByName(name)
    }

    fun getChatByPhone(phoneOther: String) = ApiRequestFlowResponse {
        chatService.getChatByPhone(phoneOther)
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

    fun postLeaveGroup(id: UUID) = ApiRequestFlowResponse {
        chatService.postLeaveGroup(id)
    }

    fun putEditPhoto(id: UUID, photo: String?) = ApiRequestFlowResponse {
        chatService.putEditPhoto(id, photo)
    }

    fun deleteChat(id: UUID) = ApiRequestFlowResponse {
        chatService.deleteChat(id)
    }

}
package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IHubService
import java.util.UUID
import javax.inject.Inject


class HubRepository @Inject constructor(
    private val hubService: IHubService,
) {
    fun joinGroup(connectionId: String, groupName: String) = ApiRequestFlowResponse {
        hubService.joinGroup(connectionId, groupName)
    }

    fun leaveGroup(connectionId: String, groupName: String) = ApiRequestFlowResponse {
        hubService.leaveGroup(connectionId, groupName)
    }

    fun sendMessage(chatId: UUID, message: String, groupName: String) = ApiRequestFlowResponse {
        hubService.sendMessage(chatId, message, groupName)
    }

    fun printGroup(groupName: String, isStart: Boolean) = ApiRequestFlowResponse {
        hubService.printGroup(groupName, isStart)
    }

    fun sendRegister(connectionId: String) = ApiRequestFlowResponse {
        hubService.sendRegister(connectionId)
    }

    fun sendNotificationGroupContact(phoneOther: String, chatId: UUID, message: String) =
        ApiRequestFlowResponse {
            hubService.sendNotificationGroupContact(phoneOther, chatId, message)
        }

    fun updateChatContact(phoneOther: String) = ApiRequestFlowResponse {
        hubService.updateChatContact(phoneOther)
    }

    fun updateChatGroup(phones: MutableList<String>) = ApiRequestFlowResponse {
        hubService.updateChatGroup(phones)
    }

    fun updateMessageContact(phoneOther: String) = ApiRequestFlowResponse {
        hubService.updateMessageContact(phoneOther)
    }

    fun updateMessageGroup(phones: MutableList<String>) = ApiRequestFlowResponse {
        hubService.updateMessageGroup(phones)
    }

    fun deletedChatContact(phoneOther: String) = ApiRequestFlowResponse {
        hubService.deletedChatContact(phoneOther)
    }

    fun deletedChatGroup(phones: MutableList<String>) = ApiRequestFlowResponse {
        hubService.deletedChatGroup(phones)
    }

}
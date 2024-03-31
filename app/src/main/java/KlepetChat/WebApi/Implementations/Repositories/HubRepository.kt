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

    fun updateChat(phoneOther: String) = ApiRequestFlowResponse {
        hubService.updateChat(phoneOther)
    }

    fun updateMessage(phoneOther: String) = ApiRequestFlowResponse {
        hubService.updateMessage(phoneOther)
    }
}
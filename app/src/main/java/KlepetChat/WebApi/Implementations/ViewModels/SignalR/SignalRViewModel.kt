package KlepetChat.WebApi.Implementations.ViewModels.SignalR

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.HubRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import androidx.lifecycle.MutableLiveData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignalRViewModel @Inject constructor(
    private val hubConnection: HubConnection,
    private val hubRepository: HubRepository,
) : BaseViewModel() {
    private val hubResponse = MutableLiveData<ApiResponse<ResponseBody>>()
    fun getConnection() = hubConnection
    private fun joinGroup(
        connectionId: String,
        groupName: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.joinGroup(connectionId, groupName)
    }

    private fun leaveGroup(
        connectionId: String,
        groupName: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.leaveGroup(connectionId, groupName)
    }

    private fun sendMessage(
        chatId: UUID, message: String, groupName: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.sendMessage(chatId, message, groupName)
    }

    private fun sendRegister(
        connectionId: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.sendRegister(connectionId)
    }

    private fun sendNotificationContact(
        phoneOther: String,
        chatId: UUID,
        message: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.sendNotificationContact(phoneOther, chatId, message)
    }

    private fun sendNotificationGroup(
        phoneOther: String,
        chatId: UUID,
        messageId: UUID,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.sendNotificationGroup(phoneOther, chatId, messageId)
    }

    private fun printGroup(
        groupName: String,
        isStart: Boolean,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.printGroup(groupName, isStart)
    }
    private fun updateChat(
        phoneOther: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.updateChat(phoneOther)
    }

    private fun updateChatInfo(
        phoneOther: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.updateChatInfo(phoneOther)
    }

    private fun updateMessageContact(
        phoneOther: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.updateMessageContact(phoneOther)
    }

    private fun updateMessageGroup(
        phoneOther: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.updateMessageGroup(phoneOther)
    }

    private fun exitChat(
        phoneOther: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        hubResponse,
        coroutineErrorHandler
    ) {
        hubRepository.exitChat(phoneOther)
    }



    fun joinGroup(groupName: String) {
        joinGroup(
            hubConnection.connectionId.toString(),
            groupName,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun sendNotificationContact(phoneOther: String, chatId: UUID, message: String) {
        sendNotificationContact(
            phoneOther,
            chatId,
            message,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun sendNotificationGroup(phoneOther: String, chatId: UUID, messageId: UUID) {
        sendNotificationGroup(
            phoneOther,
            chatId,
            messageId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun sendMessage(chatId: UUID, message: String, groupName: String) {
        sendMessage(
            chatId,
            message,
            groupName,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }


    fun leaveGroup(groupName: String) {
        leaveGroup(
            hubConnection.connectionId.toString(),
            groupName,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun printGroup(groupName: String, isStart: Boolean) {
        printGroup(
            groupName,
            isStart,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun updateChat(phoneOther: String) {
        updateChat(
            phoneOther,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun updateMessageContact(phoneOther: String) {
        updateMessageContact(
            phoneOther,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun updateMessageGroup(phoneOther: String) {
        updateMessageGroup(
            phoneOther,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    fun exitChat(phoneOther: String) {
        exitChat(
            phoneOther,
            object : ICoroutinesErrorHandler{
                override fun onError(message: String) {

                }
            })
    }



    fun start() {
        try {
            if (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
                hubConnection.start().blockingAwait()
                sendRegister(
                    hubConnection.connectionId,
                    object : ICoroutinesErrorHandler {
                        override fun onError(message: String) {

                        }
                    })
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun close(groupName: String) {
        try {
            if (hubConnection.connectionState != HubConnectionState.DISCONNECTED) {
                hubConnection.stop().blockingAwait()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}
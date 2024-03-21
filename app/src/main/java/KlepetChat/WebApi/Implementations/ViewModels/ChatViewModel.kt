package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.ChatRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : BaseViewModel() {
    private val chatsResponse = MutableLiveData<ApiResponse<MutableList<Chat>>>()
    private val chatExists = MutableLiveData<ApiResponse<Boolean>>()
    private val chatResponse = MutableLiveData<ApiResponse<Chat>>()
    private val chatImageResponse = MutableLiveData<ApiResponse<Chat>>()
    private val chatBodyResponse = MutableLiveData<ApiResponse<ResponseBody>>()
    val chats = chatsResponse
    val exists = chatExists
    val chat = chatResponse
    val chatImage = chatImageResponse

    fun getChats(coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatsResponse,
        coroutineErrorHandler
    ) {
        chatRepository.getChats()
    }

    fun getChatsByName(name: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatsResponse,
        coroutineErrorHandler
    ) {
        chatRepository.getChatsByName(name)
    }

    fun getChatByPhone(phoneOther: String, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            chatResponse,
            coroutineErrorHandler
        ) {
            chatRepository.getChatByPhone(phoneOther)
        }

    fun postFavorites(userId: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatExists,
        coroutineErrorHandler
    ) {
        chatRepository.postFavorites(userId)
    }

    fun postContact(phoneOther: String, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            chatResponse,
            coroutineErrorHandler
        ) {
            chatRepository.postContact(phoneOther)
        }

    fun postGroup(
        name: String,
        photo: String? = null,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        chatResponse,
        coroutineErrorHandler
    ) {
        chatRepository.postGroup(name, photo)
    }

    fun postJoinGroup(id: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatResponse,
        coroutineErrorHandler
    ) {
        chatRepository.postJoinGroup(id)
    }

    fun postLeaveGroup(id: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatExists,
        coroutineErrorHandler
    ) {
        chatRepository.postLeaveGroup(id)
    }

    fun putEditPhoto(id: UUID, photo: String?, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            chatImageResponse,
            coroutineErrorHandler
        ) {
            chatRepository.putEditPhoto(id, photo)
        }

    fun deleteChat(id: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatBodyResponse,
        coroutineErrorHandler
    ) {
        chatRepository.deleteChat(id)
    }
}

package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.UserRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.FIO
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.Enums.StatusTypes
import KlepetChat.WebApi.Models.Response.User
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val userResponse = MutableLiveData<ApiResponse<User>>()
    private val userStatusResponse = MutableLiveData<ApiResponse<User>>()
    private val userPhoneResponse = MutableLiveData<ApiResponse<User>>()
    private val usersResponse = MutableLiveData<ApiResponse<MutableList<User>>>()
    private val validateResponse = MutableLiveData<ApiResponse<ResponseBody>>()
    val user = userResponse
    val userEditPhone = userPhoneResponse
    val userEditStatus = userStatusResponse
    val users = usersResponse
    val validate = validateResponse

    fun validateUser(password: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        validateResponse,
        coroutineErrorHandler
    ) {
        userRepository.validateUser(password)
    }

    fun getByPhone(phone: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ) {
        userRepository.getByPhone(phone)
    }

    fun getUsersByName(name: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        usersResponse,
        coroutineErrorHandler
    ) {
        userRepository.getUsersByName(name)
    }


    fun getContactsOther(coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        usersResponse,
        coroutineErrorHandler
    ) {
        userRepository.getContactsOther()
    }

    fun getAllUserByChatId(chatId: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            usersResponse,
            coroutineErrorHandler
        ) {
            userRepository.getAllUserByChatId(chatId)
        }

    fun putFIO(fio: FIO, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ) {
        userRepository.putFIO(fio)
    }

    fun putNickname(nickname: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ) {
        userRepository.putNickname(nickname)
    }

    fun putAboutMe(aboutMe: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ) {
        userRepository.putAboutMe(aboutMe)
    }

    fun putPhoto(photo: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userResponse,
        coroutineErrorHandler
    ) {
        userRepository.putPhoto(photo)
    }

    fun putStatus(status: StatusTypes, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userStatusResponse,
        coroutineErrorHandler
    ) {
        userRepository.putStatus(status)
    }

    fun putPhone(login: Login, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        userPhoneResponse,
        coroutineErrorHandler
    ) {
        userRepository.putPhone(login)
    }

    fun postCreate(userRegister: UserRegister, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            userResponse,
            coroutineErrorHandler
        ) {
            userRepository.postCreate(userRegister)
        }

    fun deleteUser(password: String, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            validateResponse,
            coroutineErrorHandler
        ) {
            userRepository.deleteUser(password)
        }


}
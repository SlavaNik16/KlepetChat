package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IUserService
import KlepetChat.WebApi.Models.Request.FIO
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.Enums.StatusTypes
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: IUserService,
) {
    fun validateUser(password: String) = ApiRequestFlowResponse {
        userService.validateUser(password)
    }

    fun getByPhone(phone: String) = ApiRequestFlowResponse {
        userService.getByPhone(phone)
    }

    fun getUsersByName(name: String) = ApiRequestFlowResponse {
        userService.getUsersByName(name)
    }

    fun getContactsOther() = ApiRequestFlowResponse {
        userService.getContactsOther()
    }

    fun getAllUserByChatId(chatId: UUID) = ApiRequestFlowResponse {
        userService.getAllUserByChatId(chatId)
    }

    fun putFIO(fio: FIO) = ApiRequestFlowResponse {
        userService.putFIO(fio)
    }

    fun putNickname(nickname: String) = ApiRequestFlowResponse {
        userService.putNickname(nickname)
    }

    fun putAboutMe(aboutMe: String) = ApiRequestFlowResponse {
        userService.putAboutMe(aboutMe)
    }

    fun putPhoto(photo: String) = ApiRequestFlowResponse {
        userService.putPhoto(photo)
    }
    fun putStatus(status: StatusTypes) = ApiRequestFlowResponse {
        userService.putStatus(status)
    }

    fun putPhone(login: Login) = ApiRequestFlowResponse {
        userService.putPhone(login)
    }

    fun postCreate(userRegister: UserRegister) = ApiRequestFlowResponse {
        userService.postCreate(userRegister)
    }

    fun deleteUser(password: String) = ApiRequestFlowResponse {
        userService.deleteUser(password)
    }
}
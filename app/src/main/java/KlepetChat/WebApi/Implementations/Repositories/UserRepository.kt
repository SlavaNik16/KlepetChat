package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IUserService
import KlepetChat.WebApi.Models.Request.FIO
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Request.UserRegister
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: IUserService,
) {
    fun getByPhone(phone: String) = ApiRequestFlowResponse {
        userService.getByPhone(phone)
    }

    fun getContactsOther() = ApiRequestFlowResponse {
        userService.getContactsOther()
    }

    fun getAllUserByChatId(chatId:UUID) = ApiRequestFlowResponse {
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

    fun putPhone(login: Login) = ApiRequestFlowResponse {
        userService.putPhone(login)
    }

    fun postCreate(userRegister: UserRegister) = ApiRequestFlowResponse {
        userService.postCreate(userRegister)
    }
}
package KlepetChat.DataSore.Interface

import KlepetChat.DataSore.Models.UserData
import kotlinx.coroutines.flow.Flow


interface IUserDataStore {
    val userDataFlow: Flow<UserData>
    suspend fun SaveUserData(userData: UserData)
    suspend fun ClearUserData()
    suspend fun UpdateTokens(accessToken: String? = null, refreshToken: String? = null)

}
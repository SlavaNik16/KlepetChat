package KlepetChat.WebApi.Implementations.ViewModels.DataStore

import KlepetChat.DataSore.Context.DataStoreManager
import KlepetChat.DataSore.Models.UserData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    val userData = MutableLiveData<UserData?>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.userDataFlow.collect() {
                withContext(Dispatchers.Main) {
                    userData.value = it
                }
            }
        }
    }

    fun SaveUserData(userData: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.SaveUserData(userData)
        }
    }

    fun ClearUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.ClearUserData()
        }
    }

    fun UpdateTokens(accessToken: String? = null, refreshToken: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.UpdateTokens(accessToken, refreshToken)
        }
    }

}
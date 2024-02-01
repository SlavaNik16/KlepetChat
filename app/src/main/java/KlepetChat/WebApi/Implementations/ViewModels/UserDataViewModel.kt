package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.DataSore.Context.DataStoreManager
import KlepetChat.DataSore.Models.UserData
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataViewModel @Inject constructor(
    private val context: Context
): ViewModel() {

    private val dataStoreManager = DataStoreManager(context)
    val userData = MutableLiveData<UserData?>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.userDataFlow.collect() {
                withContext(Dispatchers.Main){
                    userData.value = it
                }
            }
        }
    }

    fun SaveUserData(userData: UserData){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.SaveUserData(userData)
        }
    }
    fun ClearUserData(){
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
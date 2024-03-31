package KlepetChat.WebApi.Implementations.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel(){

    val position: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

}
package KlepetChat.WebApi.Implementations

import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Класс отлавливает и отображает ошибки в правильном потоке
 * И отменяет задание при необходимости
 */
open class BaseViewModel : ViewModel() {
    private var job: Job? = null

    protected fun <T> BaseRequest(
        liveData: MutableLiveData<T>,
        errorHandler: ICoroutinesErrorHandler,
        request: () -> Flow<T>,
    ) {
        job = viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, error ->
            viewModelScope.launch(Dispatchers.Main) {
                errorHandler.onError(
                    error.localizedMessage ?: "Произошла ошибка! Пожалуйста повторите снова."
                )
            }
        }) {
            request().collect() {
                withContext(Dispatchers.Main) {
                    liveData.value = it
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.let {
            if (it.isActive) {
                it.cancel()
            }
        }
    }

}
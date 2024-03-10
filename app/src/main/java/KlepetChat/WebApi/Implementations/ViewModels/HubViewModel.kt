//package KlepetChat.WebApi.Implementations.ViewModels
//
//import KlepetChat.WebApi.Implementations.ApiResponse
//import KlepetChat.WebApi.Implementations.BaseViewModel
//import KlepetChat.WebApi.Implementations.Repositories.HubRepository
//import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
//import androidx.lifecycle.MutableLiveData
//import dagger.hilt.android.lifecycle.HiltViewModel
//import okhttp3.ResponseBody
//import javax.inject.Inject
//
//@HiltViewModel
//class HubViewModel @Inject constructor(
//    private val hubRepository: HubRepository,
//): BaseViewModel() {
//    private val hubResponse = MutableLiveData<ApiResponse<ResponseBody>>()
//    val hub = hubResponse
//
//    fun postTest(coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
//        hubResponse,
//        coroutineErrorHandler
//    ) {
//        hubRepository.postTest()
//    }
//}
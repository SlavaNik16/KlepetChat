package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.ImageRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
): BaseViewModel() {
    private val imgResponse = MutableLiveData<ApiResponse<ResponseBody>>()
    val img = imgResponse


    fun postImg(file1: MultipartBody.Part, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        imgResponse,
        coroutineErrorHandler
    ) {
        imageRepository.postImg(file1)
    }
}
package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IImageService
import okhttp3.MultipartBody
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val imageService: IImageService,
) {
    fun postImg(file1: MultipartBody.Part) = ApiRequestFlowResponse {
        imageService.postImg(file1)
    }
}
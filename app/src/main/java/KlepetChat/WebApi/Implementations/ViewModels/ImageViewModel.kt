package KlepetChat.WebApi.Implementations.ViewModels

//@HiltViewModel
//class ImageViewModel @Inject constructor(
//    private val imageRepository: ImageRepository,
//): BaseViewModel() {
//    private val imgResponse = MutableLiveData<ApiResponse<ResponseBody>>()
//    val img = imgResponse
//
//
//    fun postImg(file1:MultipartBody.Part,  coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
//        imgResponse,
//        coroutineErrorHandler
//    ) {
//        imageRepository.postImg(file1)
//    }
//}
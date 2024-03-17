package KlepetChat.Activities.DialogFragment

import KlepetChat.Activities.Chat.ChatGroupActivity
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.ProfileActivity
import KlepetChat.Adapters.UserViewItemAdapter
import KlepetChat.Image.ImageContainer
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.ImageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.RoleTypes
import KlepetChat.WebApi.Models.Response.User
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.AlertDialogGroupChatProfileBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.util.UUID

@AndroidEntryPoint
class AlertDialogGroupChatProfile : DialogFragment() {
    private var binding: AlertDialogGroupChatProfileBinding? = null
    private var alert: AlertDialog? = null
    private var userViewModel: UserViewModel? = null
    private var imageViewModel: ImageViewModel? = null
    private var chatViewModel: ChatViewModel? = null
    private var adapter: RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>? = null
    private var users: MutableList<User>? = null
    private var chatId: UUID? = null
    private var phone: String? = null
    private var file: File? = null
    private var roleType: RoleTypes =RoleTypes.User


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var alertDialog = AlertDialog.Builder(requireActivity())
        var view =
            layoutInflater.inflate(R.layout.alert_dialog_group_chat_profile, null)
        binding = AlertDialogGroupChatProfileBinding.bind(view)
        alertDialog.setView(view)
        alert = alertDialog.create()
        setListeners()
        setObserve()
        getAllUsers()
        init()
        return alert!!
    }

    private fun init() {
        binding?.name?.text = arguments?.getString(Constants.KEY_CHAT_NAME)
        Picasso.get().load(arguments?.getString(Constants.KEY_IMAGE_URL))
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(binding?.imageUser)
    }

    private fun getAllUsers() {
        userViewModel?.getAllUserByChatId(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    private fun setObserve() {

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel?.users?.observe(requireActivity()) { getUsers(it) }
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]
        imageViewModel?.img?.observe(requireActivity()) { getHttpImage(it) }
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel?.chatImage?.observe(requireActivity()) { getChat(it) }
    }

    private fun getChat(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {

                Toast.makeText(
                    requireActivity(), "Фото успешно сохранено!", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getHttpImage(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                var imageHttp = api.data.string()
                putEditPhotoChat(imageHttp)
                if (file?.exists() == true) {
                    file?.delete()
                }
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun putEditPhotoChat(photo: String?) {
        chatViewModel?.putEditPhoto(chatId!!, photo,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    private fun getUsers(api: ApiResponse<MutableList<User>>) {
        when (api) {
            is ApiResponse.Success -> {
                this.users = api.data
                adapter = UserViewItemAdapter(users!!)
                binding?.contactRecycler?.adapter = adapter
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun setListeners() {
        binding?.imageButtonBack?.setOnClickListener { onBackPress(alert!!) }
        binding?.imageUser?.setOnClickListener {  onImageClick() }
        binding?.contactRecycler?.addOnChildAttachStateChangeListener(onRecyclerAttachState())
    }
    private fun onRecyclerAttachState(): RecyclerView.OnChildAttachStateChangeListener {
        return object : RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewAttachedToWindow(view: View) {
                var position =
                    binding?.contactRecycler?.findContainingViewHolder(view)!!.adapterPosition
                view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener {
                    navigateToProfile(users!!.get(position))
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                return
            }
        }
    }

    private fun onImageClick() {
        if(roleType != RoleTypes.Admin) {
            Toast.makeText(requireActivity(), "Недостаточно прав!!!",Toast.LENGTH_SHORT).show()
            return
        }
        var photoPickerIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*")
        getAction.launch(photoPickerIntent)
    }

    private fun removeListeners() {
        binding?.imageButtonBack?.setOnClickListener(null)
        binding?.imageUser?.setOnClickListener(null)
        getAction.unregister()

    }
    private fun removeComponent(){
        users = null
        file = null
        adapter = null
        chatId = null
        binding?.contactRecycler?.adapter = null
        binding?.contactRecycler?.layoutManager = null
        binding?.contactRecycler?.recycledViewPool?.clear()
    }
    private fun removeObserve(){
        userViewModel = null
        imageViewModel = null
        chatViewModel = null
        this.viewModelStore.clear()
    }

    private fun onBackPress(alertDialog: AlertDialog) {
        alertDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        removeComponent()
        alert = null
        binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObserve()
    }


    private val getAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            var bitmap: Bitmap? = null
            if (it.resultCode == ComponentActivity.RESULT_OK) {
                val selectedImage = it?.data?.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        selectedImage
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding?.imageUser?.setImageBitmap(bitmap)
                var activity = requireActivity()
                if(activity is ChatGroupActivity){
                    var chatGroupActivity = activity as ChatGroupActivity
                    chatGroupActivity.binding?.imageChat?.setImageBitmap(bitmap)
                }

                val tempUri: Uri = ImageContainer.getImageUri(requireContext(), bitmap!!)
                file = File(ImageContainer.getRealPathFromURI(requireActivity(), tempUri))
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                val filePart =
                    MultipartBody.Part.createFormData("file", file!!.name, requestFile)
                postImg(filePart)

            }
        }

    private fun postImg(file: MultipartBody.Part) {
        imageViewModel?.postImg(file,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        requireActivity(), "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        Toast.makeText(
            requireActivity(), "Идет загрузка фото!", Toast.LENGTH_LONG
        ).show()
    }
    private fun navigateToProfile(user: User) {
        val intent = Intent(requireActivity(), ProfileActivity::class.java)
        intent.putExtra(Constants.KEY_PROFILE_VIEW,user.phone != phone)
        intent.putExtra(Constants.KEY_USER_PHONE,user.phone)
        startActivity(intent)
    }



    companion object {
        @JvmStatic
        fun newInstance(id: UUID,ph:String, title: String, role:RoleTypes = RoleTypes.User, photo: String? = "Empty") =
            AlertDialogGroupChatProfile().apply {
                arguments = Bundle().apply {
                    chatId = id
                    phone = ph
                    this.putString(Constants.KEY_CHAT_NAME, title)
                    var image = photo
                    if (photo.isNullOrBlank()) {
                        image = "Empty"
                    }
                    this.putString(Constants.KEY_IMAGE_URL, image)
                    roleType = role
                }
            }

    }
}

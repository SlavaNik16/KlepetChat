package KlepetChat.Activities

import KlepetChat.Activities.Chat.ChatContactActivity
import KlepetChat.Activities.Chat.ChatGroupActivity
import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.UserViewItemAdapter
import KlepetChat.Image.ImageContainer
import KlepetChat.Utils.TextChangedListener
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.ImageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.User
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChooseBinding
import com.example.klepetchat.databinding.AlertDialogCreateGroupBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class ChooseActivity : AppCompatActivity() {
    private var binding: ActivityChooseBinding? = null
    private var dialogBinding: AlertDialogCreateGroupBinding? = null
    private val userViewModel: UserViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>
    private lateinit var users: MutableList<User>
    private var iamgeURL: String? = null
    private lateinit var file: File
    private var isEdit = false
    private var phone:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        init()
    }

    override fun onStart() {
        super.onStart()
        getContactsOther()
    }

    private fun init() {
        val isOpenGroup = intent?.extras?.getBoolean(Constants.KEY_IS_OPEN_GROUP) ?: false
        phone = intent?.extras?.getString(Constants.KEY_USER_PHONE) ?: ""
        if (isOpenGroup) {
            onAddGroup()
        }
    }

    private fun getUsers(api: ApiResponse<MutableList<User>>) {
        when (api) {
            is ApiResponse.Success -> {
                this.users = api.data
                adapter = UserViewItemAdapter(users)
                binding?.contactRecycler?.adapter = adapter
            }

            is ApiResponse.Failure -> {
                return
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getHttpImage(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                dialogBinding?.textHelpLoadImage?.text = "Фото загружено!"
                val imageHttp = api.data.string()
                iamgeURL = imageHttp
                if (file.exists()) {
                    file.delete()
                }
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChooseActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getChat(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
               navigateToGroup(api.data)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChooseActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun setObserve() {
        userViewModel.users.observe(this) { getUsers(it) }
        imageViewModel.img.observe(this) { getHttpImage(it) }
        chatViewModel.chat.observe(this) { getChat(it) }
    }

    private fun getContactsOther() {
        userViewModel.getContactsOther(object : ICoroutinesErrorHandler {
            override fun onError(message: String) {
                Toast.makeText(
                    this@ChooseActivity, "Ошибка! ${message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
        dialogBinding = null
    }

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
        binding?.addGroup?.setOnClickListener(null)
        users.clear()
        binding?.contactRecycler?.adapter = null
        binding?.contactRecycler?.layoutManager = null
        binding?.contactRecycler?.recycledViewPool?.clear()
        binding?.inputSearch?.addTextChangedListener(null)
    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
        binding?.addGroup?.setOnClickListener { onAddGroup() }
        binding?.contactRecycler?.addOnChildAttachStateChangeListener(onContactRecyclerAttach())
        binding?.butSearch?.setOnClickListener { setSearchUsers() }
        binding?.inputSearch?.addTextChangedListener(addTextSearchChange())
    }

    private fun addTextSearchChange(): TextWatcher {
        return object : TextChangedListener<EditText>(binding?.inputSearch!!) {
            private var timer = Timer()
            private val DELAY: Long = 500
            override fun onTextChanged(target: EditText, s: Editable?) {
                if (target.text.isNullOrBlank()) {
                    if (isEdit) {
                        getContactsOther()
                    }
                    return
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                resultTextSearch()
                            }
                        }
                    },
                    DELAY
                )
            }

        }
    }

    private fun resultTextSearch() {
        isEdit = true
        userViewModel.getUsersByName(binding?.inputSearch?.text.toString(),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChooseActivity, "Error! ${message}\n", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setSearchUsers() {
        val modeTag = binding?.butSearch?.tag.toString()
        if (modeTag == Constants.KEY_TAG_SEARCH) {
            isEdit = false
            binding?.butSearch?.tag = Constants.KEY_TAG_SEARCHOFF
            binding?.butSearch?.setImageResource(R.drawable.ic_close)
            binding?.inputSearch?.visibility = View.VISIBLE
            binding?.addGroup?.visibility = View.GONE
        } else {
            binding?.butSearch?.tag = Constants.KEY_TAG_SEARCH
            binding?.butSearch?.setImageResource(R.drawable.ic_poisk)
            binding?.inputSearch?.visibility = View.GONE
            binding?.inputSearch?.setText(String())
            binding?.addGroup?.visibility = View.VISIBLE
            if (getSystemService(Context.INPUT_METHOD_SERVICE) is InputMethodManager) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
        }
    }

    private fun onContactRecyclerAttach(): RecyclerView.OnChildAttachStateChangeListener {
        return object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val position =
                    binding?.contactRecycler?.findContainingViewHolder(view)!!.adapterPosition
                view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener {
                    val user = this@ChooseActivity.users[position]
                    navigateToContact(user)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                return
            }
        }
    }
    private fun navigateToContact(user: User) {
        val intent = Intent(this@ChooseActivity, ChatContactActivity::class.java)
        intent.putExtra(Constants.KEY_USER_PHONE_OTHER, user.phone)
        intent.putExtra(Constants.KEY_CHAT_NAME, user.name)
        intent.putExtra(Constants.KEY_IMAGE_URL, user.photo)
        startActivity(intent)
    }

    private fun navigateToGroup(chat: Chat) {
        val intent = Intent(this@ChooseActivity, ChatGroupActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
        intent.putExtra(Constants.KEY_USER_PHONE, phone)
        startActivity(intent)
    }

    private fun onBackPress() {
        finish()
    }

    private fun onAddGroup() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val view =
            LayoutInflater.from(dialog.context).inflate(R.layout.alert_dialog_create_group, null)
        dialogBinding = AlertDialogCreateGroupBinding.bind(view)
        dialog.setView(view)
        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                dialog?.dismiss()
            })
        dialog.setPositiveButton("Создать",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                if (dialogBinding?.groupField?.text.isNullOrBlank()) {
                    Toast.makeText(
                        this, "Имя не должно быть пустым!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                createGroup(dialogBinding?.groupField?.text.toString(), iamgeURL)
            })

        dialogBinding?.imageChat?.setOnClickListener {
            val photoPickerIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*")
            getAction.launch(photoPickerIntent)
        }
        dialog.show()
    }

    private fun createGroup(title:String, imageURL:String?){

        chatViewModel.postGroup(title, imageURL,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChooseActivity, "Ошибка! $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private val getAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            var bitmap: Bitmap? = null
            if (it.resultCode == RESULT_OK) {
                dialogBinding?.textHelpLoadImage?.text = "Идет загрузка фото..."
                val selectedImage = it?.data?.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                dialogBinding?.imageChat?.setImageBitmap(bitmap)

                val tempUri: Uri = ImageContainer.getImageUri(applicationContext, bitmap!!)
                file = File(ImageContainer.getRealPathFromURI(this, tempUri))
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val filePart =
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                postImg(filePart)

            }
        }

    private fun postImg(file: MultipartBody.Part) {
        imageViewModel.postImg(file,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChooseActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}
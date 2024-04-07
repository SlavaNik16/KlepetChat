package KlepetChat.Activities

import KlepetChat.Activities.Chat.ChatContactActivity
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.Data.Constants.Companion.cropLength
import KlepetChat.Activities.DialogFragment.AlertDialogLoadingDelete
import KlepetChat.DataSore.Models.UserData
import KlepetChat.Image.ImageContainer
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.DataStore.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.ImageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.FIO
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Token
import KlepetChat.WebApi.Models.Response.User
import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityProfileBinding
import com.example.klepetchat.databinding.AlertDialogEditFioBinding
import com.example.klepetchat.databinding.AlertDialogEditNicknameBinding
import com.example.klepetchat.databinding.AlertDialogEditPhoneBinding
import com.example.klepetchat.databinding.AlertDialogPasswordValidateBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private var binding: ActivityProfileBinding? = null
    private var bindingEditNickname: AlertDialogEditNicknameBinding? = null
    private var bindingEditPhone: AlertDialogEditPhoneBinding? = null
    private var bindingEditFIO: AlertDialogEditFioBinding? = null
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var user: User
    private lateinit var password: String
    private lateinit var file: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        getView()
        getUser()
        init()
    }

    private fun setObserve() {
        userViewModel.user.observe(this) { getUserApi(it) }
        userViewModel.userEditPhone.observe(this) { getUserEditPhoneApi(it) }
        authViewModel.token.observe(this) { getAccessToken(it) }
        imageViewModel.img.observe(this) { getHttpImage(it) }
        chatViewModel.chat.observe(this) { AnyChatSend(it) }
        userViewModel.validate.observe(this) { getValidate(it) }
    }

    private fun init(){
        binding?.switchNotification?.isChecked = checkPermission()
    }


    private fun getValidate(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                onLoadingDeleteAcc()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ProfileActivity, "Вы ввели неверный пароль!!!", Toast.LENGTH_SHORT
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
                user.photo = imageHttp
                initProfile(user)
                if (file.exists()) {
                    file.delete()
                }
                putPhoto(imageHttp)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ProfileActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }


    private fun getAccessToken(api: ApiResponse<Token>) {
        when (api) {
            is ApiResponse.Success -> {
                userDataViewModel.SaveUserData(
                    UserData(
                        user.phone,
                        api.data.accessToken.toString(),
                        api.data.refreshToken.toString(),
                        false
                    )
                )
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ProfileActivity, "Ошибка! $api.message", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getUserEditPhoneApi(api: ApiResponse<User>) {
        when (api) {
            is ApiResponse.Success -> {
                user = api.data
                initProfile(user)
                loginEditPone(user.phone, password)
                Toast.makeText(
                    this@ProfileActivity, "Номер успешно сменен!", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ProfileActivity, "Ошибка! $api.message", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getUserApi(api: ApiResponse<User>) {
        when (api) {
            is ApiResponse.Success -> {
                user = api.data
                initProfile(user)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ProfileActivity, "Ошибка! $api.message", Toast.LENGTH_SHORT
                ).show()
                exitAuth()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getView() {
        var isView = intent.extras?.getBoolean(Constants.KEY_PROFILE_VIEW)
        viewProfile(isView!!)
    }

    private fun viewProfile(isView: Boolean) {
        binding?.inputMessageAboutMe?.isEnabled = !isView
        binding?.imageUser?.isEnabled = !isView
        binding?.editPhone?.isEnabled = !isView
        binding?.editNickname?.isEnabled = !isView
        binding?.editName?.isEnabled = !isView
        binding?.butSend?.visibility = if (!isView) View.GONE else View.VISIBLE
        binding?.butDeleteAcc?.visibility = if (isView) View.GONE else View.VISIBLE
    }


    private fun getUser() {
        var phone = intent.extras?.getString(Constants.KEY_USER_PHONE)
        onUserSend(phone!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
        bindingEditNickname = null
        bindingEditPhone = null
        bindingEditFIO = null
    }

    private fun removeListeners() {
        binding?.imageButtonBack?.setOnClickListener(null)
        binding?.editName?.setOnClickListener(null)
        binding?.editPhone?.setOnClickListener(null)
        binding?.editNickname?.setOnClickListener(null)
        binding?.inputMessageAboutMe?.onFocusChangeListener = null
        binding?.form?.setOnClickListener(null)
        binding?.butSend?.setOnClickListener(null)
        binding?.butDeleteAcc?.setOnClickListener(null)
        binding?.imageUser?.setOnClickListener(null)
        binding?.switchNotification?.setOnClickListener(null)
    }

    private fun setListeners() {
        binding?.imageButtonBack?.setOnClickListener { onBackPress() }
        binding?.imageUser?.setOnClickListener { onUserPress() }
        binding?.editName?.setOnClickListener { onEditName() }
        binding?.editPhone?.setOnClickListener { onEditPhone() }
        binding?.editNickname?.setOnClickListener { onEditNickname() }
        binding?.inputMessageAboutMe?.onFocusChangeListener = onChangeFocusAboutMe()
        binding?.form?.setOnClickListener { onClickForm() }
        binding?.butSend?.setOnClickListener { onSendMessage() }
        binding?.butDeleteAcc?.setOnClickListener { onDeleteAcc() }
        binding?.switchNotification?.setOnClickListener { onSwithNotification() }
    }

    private fun onSwithNotification(){
        if(!binding?.switchNotification!!.isChecked) {
            if(checkPermission()){
                removePermission()
            }
            return
        }
        if(!checkPermission()){
            addPermission()
        }
    }
    private fun addPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                Manifest.permission.POST_NOTIFICATIONS
            ),
            Constants.REQUEST_PERMISSION_POST_NOTIFICATION
        )
    }
    private fun removePermission() {
        val intent = Intent()
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)
        startActivity(intent)
    }
    private fun checkPermission():Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun onDeleteAcc() {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        var view =
            LayoutInflater.from(dialog.context)
                .inflate(R.layout.alert_dialog_password_validate, null)
        var bindingPassword = AlertDialogPasswordValidateBinding.bind(view)
        dialog.setView(view)
        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                dialog?.dismiss()
            })
        dialog.setPositiveButton("Сохранить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                if (bindingPassword.passwordField.length() < 8) {
                    Toast.makeText(
                        applicationContext, "Слишком маленький пароль (не меньше 8)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                password = bindingPassword.passwordField.text.toString()
                getValidateUser(password)
            })
        dialog.show()
    }

    private fun onLoadingDeleteAcc() {
        val alertDialogLoadingDelete =
            AlertDialogLoadingDelete.newInstance(password, "${user.surname} ${user.name}")
        alertDialogLoadingDelete.show(supportFragmentManager, "alertDialogLoadingDelete")
    }


    private fun getValidateUser(password: String) {
        userViewModel.validateUser(password,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    private fun onSendMessage() {
        chatViewModel.getChatByPhone(user.phone,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    private fun AnyChatSend(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                navigateToContactInChat(api.data)
            }

            is ApiResponse.Failure -> {
                navigateToContactInUser()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun navigateToContactInChat(chat: Chat) {
        val intent = Intent(this@ProfileActivity, ChatContactActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
        intent.putExtra(Constants.KEY_CHAT_NAME, "${user.surname} ${user.name}")
        intent.putExtra(Constants.KEY_IMAGE_URL, user.photo)
        intent.putExtra(Constants.KEY_USER_PHONE_OTHER, user.phone)
        startActivity(intent)
        finish()
    }

    private fun navigateToContactInUser() {
        val intent = Intent(this@ProfileActivity, ChatContactActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_NAME, user.name)
        intent.putExtra(Constants.KEY_IMAGE_URL, user.photo)
        intent.putExtra(Constants.KEY_USER_PHONE_OTHER, user.phone)
        startActivity(intent)
        finish()
    }


    private fun onClickForm() {
        binding?.inputMessageAboutMe?.clearFocus()
    }

    private fun onChangeFocusAboutMe(): OnFocusChangeListener {
        return OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                putAboutMe(binding?.inputMessageAboutMe?.text.toString() ?: "")
                if (getSystemService(Context.INPUT_METHOD_SERVICE) is InputMethodManager) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }

    private fun putPhoto(imageHttp: String) {
        userViewModel.putPhoto(imageHttp,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun loginEditPone(phone: String, password: String) {
        authViewModel.login(
            Login(phone, password),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun putAboutMe(aboutMe: String) {
        userViewModel.putAboutMe(aboutMe,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onEditNickname() {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        var view =
            LayoutInflater.from(dialog.context).inflate(R.layout.alert_dialog_edit_nickname, null)
        bindingEditNickname = AlertDialogEditNicknameBinding.bind(view)
        bindingEditNickname?.nicknameField?.setText(binding?.textNickname?.text.toString())
        dialog.setView(view)
        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                dialog?.dismiss()
            })
        dialog.setPositiveButton("Сохранить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                if (bindingEditNickname?.nicknameField?.text.isNullOrBlank()) {
                    Toast.makeText(
                        this@ProfileActivity, "Имя пользователя не дожно быть пустым!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                putNickname(bindingEditNickname?.nicknameField?.text.toString())
            })
        dialog.show()
    }

    private fun onEditPhone() {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        var view =
            LayoutInflater.from(dialog.context).inflate(R.layout.alert_dialog_edit_phone, null)
        bindingEditPhone = AlertDialogEditPhoneBinding.bind(view)
        dialog.setView(view)
        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                dialog?.dismiss()
            })
        dialog.setPositiveButton("Сохранить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                var password = bindingEditPhone!!.passwordField
                var phone = bindingEditPhone!!.phoneField;
                if (password.length() < 8) {
                    Toast.makeText(
                        applicationContext, "Слишком маленький пароль (не меньше 8)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                if (phone.text!!.length < 11) {
                    Toast.makeText(
                        applicationContext, "Такого номера телефона не существует!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                onEditPhone(
                    phone.text.toString(),
                    password.text.toString()
                )
            })
        dialog.show()
    }

    private fun onEditPhone(phone: String, password: String) {
        this.password = password
        userViewModel.putPhone(
            Login(phone, password),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onEditName() {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        var view =
            LayoutInflater.from(dialog.context).inflate(R.layout.alert_dialog_edit_fio, null)
        bindingEditFIO = AlertDialogEditFioBinding.bind(view)
        var fio = binding?.textFIO?.text.toString().split(" ")
        bindingEditFIO?.surnameField?.setText(fio[0])
        bindingEditFIO?.nameField?.setText(fio[1])
        dialog.setView(view)
        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                dialog?.dismiss()
            })
        dialog.setPositiveButton("Сохранить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                if (bindingEditFIO?.nameField?.text.isNullOrBlank() ||
                    bindingEditFIO?.surnameField?.text.isNullOrBlank()
                ) {
                    Toast.makeText(
                        this@ProfileActivity, "Фамилия и имя, не должны быть пустыми!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                putFIO(
                    bindingEditFIO?.surnameField?.text.toString(),
                    bindingEditFIO?.nameField?.text.toString()
                )
            })
        dialog.show()
    }

    private fun onUserSend(phone: String) {
        userViewModel.getByPhone(phone,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun putNickname(nickname: String) {
        userViewModel.putNickname(nickname,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun putFIO(surname: String, name: String) {
        userViewModel.putFIO(FIO(surname, name),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onBackPress() {
        finish()
    }

    private fun onUserPress() {
        var photoPickerIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*")
        getAction.launch(photoPickerIntent)
    }

    private val getAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            var bitmap: Bitmap? = null
            if (it.resultCode == RESULT_OK) {
                val selectedImage = it?.data?.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
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
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun exitAuth() {
        userDataViewModel.ClearUserData()
        var intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initProfile(user: User) {
        var fio = "${user.surname} ${user.name}"
        binding?.textFIO?.text = fio
        binding?.textFIOEdit?.text = fio.cropLength(Constants.TEXT_SIZE_CROP_SHORT)
        binding?.textPhone?.text = user.phone
        binding?.textNickname?.text = user.nickName?.cropLength(Constants.TEXT_SIZE_CROP_SHORT)
        binding?.inputMessageAboutMe?.setText(user.aboutMe)
        if (user.photo.isNullOrBlank()) {
            return
        }
        Picasso.get()
            .load(user.photo)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(binding?.imageUser)
    }
}

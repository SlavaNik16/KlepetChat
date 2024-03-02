package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.UserViewItemAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.ImageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.User
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChooseBinding
import com.example.klepetchat.databinding.AlertDialogCreateGroupBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class ChooseActivity : ComponentActivity() {
    private lateinit var binding: ActivityChooseBinding
    private lateinit var dialogBinding: AlertDialogCreateGroupBinding
    private val userViewModel: UserViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>
    private lateinit var users: MutableList<User>
    private var iamgeURL:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        userViewModel.users.observe(this){
            when (it) {
                is ApiResponse.Success -> {
                    this.users = it.data
                    adapter = UserViewItemAdapter(this, users)
                    binding.contactRecycler.adapter = adapter
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@ChooseActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }

        userViewModel.getContactsOther(object : ICoroutinesErrorHandler {
            override fun onError(message: String) {
                Toast.makeText(
                    this@ChooseActivity, "Ошибка! ${message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
        imageViewModel.img.observe(this){
            when (it) {
                is ApiResponse.Success -> {
                    dialogBinding.textHelpLoadImage.text = "Фото загружено!"
                  var imageHttp = it.data.string()
                    iamgeURL = imageHttp
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@ChooseActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }
        binding.contactRecycler.addOnChildAttachStateChangeListener(
            object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    var position =
                        binding.contactRecycler.findContainingViewHolder(view)!!.adapterPosition
                    view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener {
                        var user = this@ChooseActivity.users[position]
                        val intent = Intent(this@ChooseActivity, ChatActivity::class.java)
                        intent.putExtra(Constants.KEY_USER_PHONE, user.phone)
                        intent.putExtra(Constants.KEY_CHAT_NAME, user.name)
                        intent.putExtra(Constants.KEY_IMAGE_URL, user.photo)
                        intent.putExtra(Constants.KEY_CHAT_TYPE, ChatTypes.Contact)
                        intent.putExtra(Constants.KEY_IS_PREV, true)
                        startActivity(intent)
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {
                    return
                }
            })

    }

    private fun setListeners() {
        binding.back.setOnClickListener { onBackPress() }
        binding.addGroup.setOnClickListener{onAddGroup()}
    }
    private fun onBackPress(){
        var intent = Intent(this@ChooseActivity, MainActivity::class.java)
        startActivity(intent)
    }
    private fun onAddGroup(){
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        var view = LayoutInflater.from(dialog.context).inflate(R.layout.alert_dialog_create_group, null)
        dialogBinding = AlertDialogCreateGroupBinding.bind(view)
        dialog.setView(view)
        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialog: DialogInterface?, _ ->
                dialog?.dismiss()
            })
        dialog.setPositiveButton("Создать",
            DialogInterface.OnClickListener { dialog:DialogInterface?, _ ->
                if(dialogBinding.groupField.text.isNullOrBlank()){
                    return@OnClickListener
                }
                Log.d("Post", dialogBinding.groupField.text.toString())
                Log.d("Post", "$iamgeURL")
                chatViewModel.postGroup(
                    dialogBinding.groupField.text.toString(),
                    iamgeURL,
                    object : ICoroutinesErrorHandler {
                        override fun onError(message: String) {
                            Toast.makeText(this@ChooseActivity,"Ошибка! $message",
                                Toast.LENGTH_SHORT).show()
                        }
                    })
            })

        dialogBinding.imageChat.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*")
            getAction.launch(photoPickerIntent)
        }
        dialog.show()
    }

    private val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        var bitmap: Bitmap? = null
        if (it.resultCode == RESULT_OK) {
            dialogBinding.textHelpLoadImage.text = "Идет загрузка фото..."
            val selectedImage = it?.data?.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            dialogBinding.imageChat.setImageBitmap(bitmap)

            val tempUri: Uri = getImageUri(applicationContext, bitmap!!)
            val file: File = File(getRealPathFromURI(tempUri))
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val filePart =
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            imageViewModel.postImg(filePart,
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
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        var largeImagePath = ""
        try {
            cursor!!.moveToFirst()
            val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
            largeImagePath = cursor.getString(idx)
        }finally {
            cursor?.close()
        }
        return largeImagePath
    }
}
package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.UserViewItemAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.User
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import java.io.File


@AndroidEntryPoint
class ChooseActivity : ComponentActivity() {
    private lateinit var binding: ActivityChooseBinding
    private lateinit var dialogBinding: AlertDialogCreateGroupBinding
    private val userViewModel: UserViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    //private val imageViewModel: ImageViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>
    private lateinit var users: MutableList<User>
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
//        imageViewModel.img.observe(this){
//            when (it) {
//                is ApiResponse.Success -> {
//                  var imageHttp = it.data.string()
//                    Log.d("Image", imageHttp)
//                }
//
//                is ApiResponse.Failure -> {
//                    Toast.makeText(
//                        this@ChooseActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                is ApiResponse.Loading -> {
//                    return@observe
//                }
//            }
//        }
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
                chatViewModel.postGroup(dialogBinding.groupField.text.toString(),
                    object : ICoroutinesErrorHandler {
                        override fun onError(message: String) {
                            Toast.makeText(this@ChooseActivity,"Ошибка! $message",
                                Toast.LENGTH_SHORT).show()
                        }
                    })
            })

        dialogBinding.imageChat.setOnClickListener {
            var photoPickerIntent = Intent(MediaStore.ACTION_PICK_IMAGES);
            getAction.launch(photoPickerIntent)
        }
        dialog.show()
    }

    private val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            var chosenImageUri: Uri? = it.data?.data;
            Log.d("Image", chosenImageUri.toString())
            var file = File(chosenImageUri.toString())
            val requestFile=
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val filePart =
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            dialogBinding.imageChat.setImageBitmap(
                BitmapFactory.decodeFile(file.absolutePath + file.name)
            )
//            imageViewModel.postImg(filePart,
//                object : ICoroutinesErrorHandler {
//                    override fun onError(message: String) {
//                        Toast.makeText(this@ChooseActivity, "Ошибка $message",
//                            Toast.LENGTH_SHORT).show()
//                    }
//                })
        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK)
//        {
//            var chosenImageUri: Uri? = data?.data;
//            Log.d("Image", chosenImageUri.toString())
//            var file = File(chosenImageUri?.path.toString())
//            val requestFile= RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
//            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
//
//        }
//    }
}
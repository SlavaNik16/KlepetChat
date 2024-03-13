package KlepetChat.Activities.DialogFragment

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.UserViewItemAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.User
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.AlertDialogGroupChatProfileBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AlertDialogGroupChatProfile : DialogFragment() {
    private var binding: AlertDialogGroupChatProfileBinding? = null
    private val userViewModel: UserViewModel by activityViewModels()
    private var alert: AlertDialog? = null
    private lateinit var adapter: RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>
    private lateinit var users: MutableList<User>
    private lateinit var chatId: UUID
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

    private fun init(){
        binding?.name?.text = arguments?.getString(Constants.KEY_CHAT_NAME)
        Picasso.get().load(arguments?.getString(Constants.KEY_IMAGE_URL))
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(binding?.imageUser)
    }
    private fun getAllUsers(){
        userViewModel.getAllUserByChatId(chatId,
            object : ICoroutinesErrorHandler{
                override fun onError(message: String) {

                }
            })
    }

    private fun setObserve(){
        userViewModel.users.observe(this){ getUsers(it) }
    }
    private fun getUsers(api: ApiResponse<MutableList<User>>) {
        when (api) {
            is ApiResponse.Success -> {
                this.users = api.data
                adapter = UserViewItemAdapter(users)
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
    }

    private fun removeListeners() {
        binding?.imageButtonBack?.setOnClickListener(null)
    }

    private fun onBackPress(alertDialog: AlertDialog) {
        alertDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        alert = null
        binding = null
    }


    companion object {
        @JvmStatic
        fun newInstance(id: UUID, title:String, photo:String? = "Empty") =
            AlertDialogGroupChatProfile().apply {
                arguments = Bundle().apply {
                    chatId = id
                    this.putString(Constants.KEY_CHAT_NAME, title)
                    var image = photo
                    if(photo.isNullOrBlank()){
                        image = "Empty"
                    }
                    this.putString(Constants.KEY_IMAGE_URL, image)
                }
            }

    }
}
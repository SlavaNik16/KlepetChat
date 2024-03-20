package KlepetChat.Activities.DialogFragment

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.RoleTypes
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.klepetchat.R
import com.example.klepetchat.databinding.AlertDialogLoadingBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.util.UUID


@AndroidEntryPoint
class AlertDialogLoadingDelete : DialogFragment() {
    private var binding: AlertDialogLoadingBinding? = null
    private var chatViewModel: ChatViewModel? = null
    private var messageViewModel: MessageViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var password: String? = null
    private var chats: MutableList<Chat>? = null
    private var index: Int = 0
    private var resultBar: Int = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        dialog.setTitle("Удаление данных")
        dialog.setMessage("Внимание! Не выключайте экран!!!")
        dialog.setCancelable(false)
        var view =
            layoutInflater.inflate(R.layout.alert_dialog_loading, null)
        binding = AlertDialogLoadingBinding.bind(view)
        dialog.setView(view)
        var alert = dialog.create()
        alert.setCanceledOnTouchOutside(false)
        LoadingEdit(0, "Начинается удаление аккаунта")
        setListeners()
        setObserve()
        getChats()
        return alert
    }

    private fun setListeners() {
        binding?.butYes?.setOnClickListener { deleteAcc() }
        binding?.butNo?.setOnClickListener { canselDeleteAcc() }
    }
    private fun setObserve() {
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel?.chats?.observe(requireActivity()) { getChatsApi(it) }
        chatViewModel?.deleteChat?.observe(requireActivity()) { deleteChatApi(it) }
        chatViewModel?.exists?.observe(requireActivity()) { leaveGroupApi(it) }
        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        messageViewModel?.exist?.observe(requireActivity()) { deleteMessageApi(it) }
    }
    private fun removeListeners() {
        binding?.butYes?.setOnClickListener(null)
        binding?.butNo?.setOnClickListener(null)
    }

    private fun removeComponent() {
        password= null
        chats = null
    }

    private fun removeObserve() {
        userViewModel = null
        messageViewModel = null
        chatViewModel = null
        this.viewModelStore.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        removeComponent()
        binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObserve()
    }

    private fun canselDeleteAcc() {
        dismiss()
    }

    private fun deleteAcc() {
        visibleButYesNo(false)
    }
    private fun visibleButYesNo(visible:Boolean){
        binding?.butYesNo?.visibility = if(visible) View.VISIBLE else View.GONE
    }

    private fun LoadingEdit(percent: Int, text: String) {
        binding?.loadingBar?.progress = percent
        binding?.loadingProc?.text = percent.toString()
        binding?.loadingText?.text = text
    }

    private fun getChats() {
        chatViewModel?.getChats(
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    private fun deleteChat(chatId: UUID) {
        chatViewModel?.deleteChat(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                }
            })
    }

    private fun leaveGroup(chatId: UUID) {
        chatViewModel?.postLeaveGroup(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                }
            })

    }

    private fun deleteMessage(chatId: UUID) {
        messageViewModel?.deleteMessages(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                }
            })
    }




    private fun getChatsApi(api: ApiResponse<MutableList<Chat>>) {
        when (api) {
            is ApiResponse.Success -> {
                LoadingEdit(10, "Получение всех данных...")
                deleteChats(api.data)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun deleteChatApi(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                var i = binding?.loadingBar?.progress!!
                LoadingEdit(i + resultBar, "Чат успешно удален!!!")
                deleteUser()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                var chat = chats?.get(index)
                LoadingEdit(binding?.loadingBar?.progress!!, "Удаление чата ${chat!!.name}...")
            }
        }
    }

    private fun deleteUser(){
        if(++index >= chats!!.count()){
            LoadingEdit(binding?.loadingBar?.progress!!, "Вы точно уверены, что хотите удалить аккаунт!!!")
            visibleButYesNo(true)
            return
        }
        var chat = chats?.get(index)
        deleteMessage(chat!!.id)
    }
    private fun leaveGroupApi(api: ApiResponse<Boolean>) {
        when (api) {
            is ApiResponse.Success -> {
                var i =binding?.loadingBar?.progress!!
                LoadingEdit(i + resultBar, "Успешный выход из чата!!!")
                deleteUser()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                var chat = chats?.get(index)
                LoadingEdit(binding?.loadingBar?.progress!!, "Выход из чата ${chat!!.name}...")
            }
        }
    }

    private fun deleteMessageApi(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                var i =binding?.loadingBar?.progress!!
                var chat = chats?.get(index)
                LoadingEdit(i + resultBar, "Все сообщения удалены!!!")
                if (chat?.roleType == RoleTypes.Admin) {
                    deleteChat(chat.id)
                } else {
                    leaveGroup(chat!!.id)
                }
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireActivity(), "${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                var chat = chats?.get(index)
                LoadingEdit(binding?.loadingBar?.progress!!, "Удаление сообщений чата ${chat!!.name}...")
            }
        }
    }

    private fun deleteChats(chats: MutableList<Chat>) {
        this.chats = chats
        var countChats = chats.count()
        if (countChats <= 0){
            deleteUser()
        }
        var i = binding?.loadingBar?.progress!!;
        resultBar = (((75 - i) / countChats)/2)
        index = 0
        var chat = chats[index]
        deleteMessage(chat.id)
        return
    }


    companion object {
        @JvmStatic
        fun newInstance(pas: String) =
            AlertDialogLoadingDelete().apply {
                arguments = Bundle().apply {
                    password = pas
                }
            }

    }
}
package KlepetChat.Activities.DialogFragment

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.RoleTypes
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.klepetchat.R
import com.example.klepetchat.databinding.AlertDialogLoadingBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlertDialogLoadingDelete : DialogFragment() {
    private var binding: AlertDialogLoadingBinding? = null
    private var chatViewModel: ChatViewModel? = null
    private var password: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        dialog.setTitle("Удаление данных")
        dialog.setMessage("Внимание! Не выключайте экран!!!")
        var view =
            layoutInflater.inflate(R.layout.alert_dialog_loading, null)
        binding = AlertDialogLoadingBinding.bind(view)
        dialog.setView(view)
        var alert = dialog.create()
        LoadingEdit(0, "Начинается удаление аккаунта")
        setObserve()
        getChats()
        return alert
    }

    private fun LoadingEdit(percent:Int,  text:String){
        binding?.loadingBar?.progress =percent
        binding?.loadingProc?.text = percent.toString()
        binding?.loadingText?.text = text
    }
    private fun getChats(){
        chatViewModel?.getChats(
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
    }
    /*
    private fun deleteChat(chatId: UUID){
        chatViewModel.deleteChat(chatId,
            object : ICoroutinesErrorHandler{
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
    }
    private fun deleteMessage(chatId: UUID){
        messa.de(chatId,
            object : ICoroutinesErrorHandler{
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
    }
    */


    private fun setObserve() {
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel?.chats?.observe(requireActivity()) { getChatsApi(it) }
    }
    private fun getChatsApi(api: ApiResponse<MutableList<Chat>>){
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
    private fun deleteChats(chats:MutableList<Chat>){
        var i = binding?.loadingBar?.progress!!;
        var count = chats.count()
        var result = (75 - i) / count
        var text = String()
        for (chat in chats){
             i = binding?.loadingBar?.progress!!;
            if(chat.roleType == RoleTypes.Admin){
               text = "Удаление чата ${chat.name}..."
            }else{
                text = "Выход из чата ${chat.name}..."
            }
            LoadingEdit(i + result, text)
            Thread.sleep(1000)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(pas:String) =
            AlertDialogLoadingDelete().apply {
                arguments = Bundle().apply {
                    password = pas
                }
            }

    }
}
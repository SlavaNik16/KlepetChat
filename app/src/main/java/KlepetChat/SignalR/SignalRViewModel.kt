//package KlepetChat.SignalR
//
//import android.content.Context
//import android.widget.Toast
//import androidx.lifecycle.ViewModel
//import com.microsoft.signalr.HubConnection
//import com.microsoft.signalr.HubConnectionState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class SignalRViewModel @Inject constructor(
//    private val hubConnection: HubConnection
//):ViewModel() {
//
//    fun start(){
//        try {
//            if(hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
//                hubConnection.start().blockingAwait()
//
//                hubConnection.invoke("Test", null)
//            }
//        }catch (ex:Exception){
//            ex.printStackTrace()
//        }
//    }
//    fun close() {
//        hubConnection.stop()
//    }
//
//    fun sendMessage() {
//        if(hubConnection.connectionState == HubConnectionState.CONNECTED) {
//            hubConnection.send("Test", null)
//        }
//    }
//    fun answerMessage(context: Context) {
//        hubConnection.on("Test", {
//            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//        },String::class.java)
//    }
//}
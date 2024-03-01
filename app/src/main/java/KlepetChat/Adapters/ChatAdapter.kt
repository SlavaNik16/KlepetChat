package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.Message
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ItemContainerReceivedMessageBinding
import com.example.klepetchat.databinding.ItemContainerSendMessageBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var messageViewItems: MutableList<Message>
    private lateinit var phone:String

    var VIEW_TYPE_SENT = 1
    var VIEW_TYPE_RECEIVED = 2

    constructor(context: Context, messageViewItems:MutableList<Message>,phone:String):this(){
        this.context = context;
        this.messageViewItems = messageViewItems;
        this.phone = phone
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_SENT){
            var sendMessageView: View =
                LayoutInflater.from(context).inflate(R.layout.item_container_send_message,
                    parent, false)
            return SentMessageViewHolder(sendMessageView)
        }else{
            var receivedMessageView: View =
                LayoutInflater.from(context).inflate(R.layout.item_container_received_message,
                    parent, false)
            return ReceivedMessageViewHolder(receivedMessageView)
        }
    }

    override fun getItemCount(): Int {
        return messageViewItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if(getItemViewType(position)== VIEW_TYPE_SENT){
           if(holder is SentMessageViewHolder){
               holder.setData(messageViewItems[position])
           }
       }else{
           if(holder is ReceivedMessageViewHolder){
               holder.setData(messageViewItems[position])
           }
       }
    }

    override fun getItemViewType(position: Int): Int {
        if(messageViewItems[position].phone.equals(phone)){
            return VIEW_TYPE_SENT
        }else{
            return VIEW_TYPE_RECEIVED
        }
    }
    class SentMessageViewHolder : RecyclerView.ViewHolder{
        var binding: ItemContainerSendMessageBinding
        constructor(itemView: View):super(itemView){
            binding = ItemContainerSendMessageBinding.bind(itemView)
        }

        fun setData(message:Message) {
            binding.textMessage.text = message.text
            binding.textDateTime.text = getReadableDateTime(message.createdAt)
        }
    }

    class ReceivedMessageViewHolder : RecyclerView.ViewHolder{
        var binding: ItemContainerReceivedMessageBinding
        constructor(itemView: View):super(itemView){
            binding = ItemContainerReceivedMessageBinding.bind(itemView)
        }

        fun setData(message:Message){
            binding.textMessage.text = message.text
            binding.textDateTime.text = getReadableDateTime(message.createdAt)
            if(message.photo.toString().isBlank()){
                binding.imageProfile.setBackgroundResource(R.drawable.baseline_account_circle_24)
                return
            }
            Picasso.get()
                .load(message.photo)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(binding.imageProfile)
        }
    }

    companion object {
        fun getReadableDateTime(date: Date):String{
            return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
        }
    }


}
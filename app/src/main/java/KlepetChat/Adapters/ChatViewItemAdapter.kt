package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ChatViewItemBinding
import com.squareup.picasso.Picasso

class ChatViewItemAdapter() : RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>() {

    lateinit var context: Context
    lateinit var chatViewItems: MutableList<User>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewItemHolder {
        var chatView: View = LayoutInflater.from(context).inflate(R.layout.chat_view_item, parent, false)
        return  ChatViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    override fun onBindViewHolder(holder: ChatViewItemHolder, position: Int) {
        holder.binding.textNameChat.text =
            "${chatViewItems[position].surname} ${chatViewItems[position].name}"
        Picasso.get()
            .load(chatViewItems[position].photo)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(holder.binding.imageUser)

        holder.binding.textDesc.text = chatViewItems[position].aboutMe
    }

    class ChatViewItemHolder : RecyclerView.ViewHolder{
        lateinit var binding: ChatViewItemBinding
        constructor(itemView: View):super(itemView){
            binding = ChatViewItemBinding.bind(itemView)
        }

    }


}
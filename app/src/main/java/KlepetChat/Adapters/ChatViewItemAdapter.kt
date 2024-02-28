package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
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
    lateinit var chatViewItems: MutableList<Chat>

    constructor(context: Context, chatViewItems:MutableList<Chat>):this(){
        this.context = context;
        this.chatViewItems = chatViewItems;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewItemHolder {
        var chatView: View = LayoutInflater.from(context).inflate(R.layout.chat_view_item, parent, false)
        return  ChatViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    override fun onBindViewHolder(holder: ChatViewItemHolder, position: Int) {
        holder.binding.DataChatId.text = chatViewItems[position].id.toString()
        holder.binding.textNameChat.text = chatViewItems[position].name
        holder.binding.textDesc.text = ""
        if(chatViewItems[position].messages.size != 0){
            holder.binding.textDesc.text = chatViewItems[position].messages[0].text
        }
        var resourceTypeChat =
            when(chatViewItems[position].chatType){
                ChatTypes.Favorites -> R.drawable.ic_favourites
                ChatTypes.Contact -> R.drawable.ic_person_contact
                ChatTypes.Group -> R.drawable.ic_add_groups
            }
        holder.binding.imageTypeChat.setBackgroundResource(resourceTypeChat)

        Picasso.get()
            .load(chatViewItems[position].photo)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(holder.binding.imageUser)
    }

    class ChatViewItemHolder : RecyclerView.ViewHolder{
        lateinit var binding: ChatViewItemBinding
        constructor(itemView: View):super(itemView){
            binding = ChatViewItemBinding.bind(itemView)
        }

    }


}
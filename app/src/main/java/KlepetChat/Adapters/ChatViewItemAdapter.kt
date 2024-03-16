package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ChatViewItemBinding
import com.squareup.picasso.Picasso

class ChatViewItemAdapter() : RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>() {

    lateinit var chatViewItems: MutableList<Chat>

    constructor(chatViewItems: MutableList<Chat>) : this() {
        this.chatViewItems = chatViewItems;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewItemHolder {
        var chatView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_view_item, parent, false)
        return ChatViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    override fun onBindViewHolder(holder: ChatViewItemHolder, position: Int) {
        holder.binding?.textName?.text = chatViewItems[position].name
        holder.binding?.textDesc?.text = String()
        if (!chatViewItems[position].lastMessage.isNullOrBlank()) {
            holder.binding?.textDesc?.text = chatViewItems[position].lastMessage
        }
        if (!chatViewItems[position].photo.isNullOrBlank()) {
            Picasso.get()
                .load(chatViewItems[position].photo)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(holder.binding?.imageChat)
        }
        var resourceTypeChat =
            when (chatViewItems[position].chatType) {
                ChatTypes.Favorites -> {
                    holder.binding?.imageChat?.setImageResource(R.drawable.favorites_icon)
                    R.drawable.ic_favourites
                }

                ChatTypes.Contact -> R.drawable.ic_person_contact
                ChatTypes.Group -> R.drawable.ic_add_groups
            }
        holder.binding?.imageTypeChat?.setBackgroundResource(resourceTypeChat)

    }

    class ChatViewItemHolder : RecyclerView.ViewHolder {
        var binding: ChatViewItemBinding? = null

        constructor(itemView: View) : super(itemView) {
            binding = ChatViewItemBinding.bind(itemView)
        }

    }
}
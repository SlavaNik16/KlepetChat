package KlepetChat.Adapters

import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.Data.Constants.Companion.cropLength
import KlepetChat.WebApi.Models.Response.User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.UserViewItemBinding
import com.squareup.picasso.Picasso

class UserViewItemAdapter() : RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>() {

    lateinit var chatViewItems: MutableList<User>

    constructor(chatViewItems: MutableList<User>) : this() {
        this.chatViewItems = chatViewItems;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewItemHolder {
        var chatView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_view_item, parent, false)
        return UserViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    override fun onBindViewHolder(holder: UserViewItemHolder, position: Int) {
        holder.binding.textName.text =
            "${chatViewItems[position].surname} ${chatViewItems[position].name}"
                .cropLength(Constants.TEXT_SIZE_CROP_DESCRIPTION)
        holder.binding.textDesc.text = String()
        if (!chatViewItems[position].phone.isNullOrBlank()) {
            holder.binding.textDesc.text = chatViewItems[position].phone
        }

        var picasso = Picasso.get()
        if (!chatViewItems[position].photo.isNullOrBlank()) {
            picasso
                .load(chatViewItems[position].photo)
                .placeholder(R.drawable.ic_chat_user)
                .error(R.drawable.ic_chat_user)
                .into(holder.binding.imageUser)
            return
        }
        holder.binding.imageUser.setImageResource(R.drawable.ic_chat_user)
    }

    class UserViewItemHolder : RecyclerView.ViewHolder {
        var binding: UserViewItemBinding

        constructor(itemView: View) : super(itemView) {
            binding = UserViewItemBinding.bind(itemView)
        }
    }
}
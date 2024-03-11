package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.User
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.UserViewItemBinding
import com.squareup.picasso.Picasso

class UserViewItemAdapter() : RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>() {

    lateinit var context: Context
    lateinit var chatViewItems: MutableList<User>

    constructor(context: Context, chatViewItems: MutableList<User>) : this() {
        this.context = context;
        this.chatViewItems = chatViewItems;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewItemHolder {
        var chatView: View =
            LayoutInflater.from(context).inflate(R.layout.user_view_item, parent, false)
        return UserViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewItemHolder, position: Int) {
        holder.binding.textName.text =
            "${chatViewItems[position].surname} ${chatViewItems[position].name}"
        holder.binding.textDesc.text = String()
        if (!chatViewItems[position].phone.isNullOrBlank()) {
            holder.binding.textDesc.text = chatViewItems[position].phone
        }

        var picasso = Picasso.get()
        if (chatViewItems[position].photo.isNullOrBlank()) {
            picasso
                .load(R.drawable.baseline_account_circle_24)
                .into(holder.binding.imageUser)
            return
        }
        picasso
            .load(chatViewItems[position].photo)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(holder.binding.imageUser)
    }

    class UserViewItemHolder : RecyclerView.ViewHolder {
        var binding: UserViewItemBinding

        constructor(itemView: View) : super(itemView) {
            binding = UserViewItemBinding.bind(itemView)
        }
    }
}
package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.User
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ChatViewItemBinding
import com.squareup.picasso.Picasso
class UserViewItemAdapter() : RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>() {

    lateinit var context: Context
    lateinit var chatViewItems: MutableList<User>

    constructor(context: Context, chatViewItems:MutableList<User>):this(){
        this.context = context;
        this.chatViewItems = chatViewItems;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewItemHolder {
        var chatView: View = LayoutInflater.from(context).inflate(R.layout.chat_view_item, parent, false)
        return  UserViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewItemHolder, position: Int) {
        holder.binding.DataUserId.text = chatViewItems[position].id.toString()
        holder.binding.textNameChat.text =
            "${chatViewItems[position].surname} ${chatViewItems[position].name}"
        holder.binding.textDesc.text = chatViewItems[position].aboutMe
        holder.binding.imageTypeChat.visibility = View.INVISIBLE
        var picasso =  Picasso.get()
        if(chatViewItems[position].photo.toString().isBlank()){
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

    class UserViewItemHolder : RecyclerView.ViewHolder{
        lateinit var binding: ChatViewItemBinding
        constructor(itemView: View):super(itemView){
            binding = ChatViewItemBinding.bind(itemView)
        }
    }
}
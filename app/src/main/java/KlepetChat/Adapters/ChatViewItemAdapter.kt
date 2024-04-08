package KlepetChat.Adapters

import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.Data.Constants.Companion.cropLength
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ChatViewItemBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class ChatViewItemAdapter() : RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>() {

    private lateinit var chatViewItems: MutableList<Chat>

    constructor(chatViewItems: MutableList<Chat>) : this() {
        this.chatViewItems = chatViewItems;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewItemHolder {
        val chatView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_view_item, parent,
                false)
        return ChatViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    override fun onBindViewHolder(holder: ChatViewItemHolder, position: Int) {
        val name = chatViewItems[position].name
        holder.binding?.textName?.text = name.cropLength(Constants.TEXT_SIZE_CROP_NAME)
        holder.binding?.textDesc?.text = String()
        if (!chatViewItems[position].lastMessage.isNullOrBlank()) {
            val lastMessage = chatViewItems[position].lastMessage ?: " "
            holder.binding?.textDesc?.text =
                lastMessage.cropLength(Constants.TEXT_SIZE_CROP_DESCRIPTION)
            val date = chatViewItems[position].lastDate
            val dateLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val period: Period = Period.between(dateLocal, LocalDate.now())
            if (period.days <= 0 && period.months == 0 && period.years == 0) {
                holder.binding?.textDate?.text =
                    getReadableDateTimeNow(chatViewItems[position].lastDate)
            } else if (period.days in 1..7 && period.months == 0 && period.years == 0) {
                holder.binding?.textDate?.text =
                    getReadableDateTimeWeek(chatViewItems[position].lastDate)
            } else if (period.months in 1..12 && period.years == 0) {
                holder.binding?.textDate?.text =
                    getReadableDateTimeMonth(chatViewItems[position].lastDate)
            } else {
                holder.binding?.textDate?.text =
                    getReadableDateTimeOther(chatViewItems[position].lastDate)
            }

        }
        val path = if(chatViewItems[position].photo.isNullOrBlank()) "empty" else
            chatViewItems[position].photo
        val resourceTypeChat =
            when (chatViewItems[position].chatType) {
                ChatTypes.Favorites -> {
                    holder.binding?.imageChat?.setImageResource(R.drawable.ic_favorite)
                    R.drawable.ic_favourites
                }

                ChatTypes.Contact ->{
                    Picasso.get()
                        .load(path)
                        .placeholder(R.drawable.ic_chat_user)
                        .error(R.drawable.ic_chat_user)
                        .into(holder.binding?.imageChat)
                    R.drawable.ic_person_contact
                }
                ChatTypes.Group ->{
                    Picasso.get()
                        .load(path)
                        .placeholder(R.drawable.ic_group)
                        .error(R.drawable.ic_group)
                        .into(holder.binding?.imageChat)
                    R.drawable.ic_add_groups
                }
            }
        holder.binding?.imageTypeChat?.setBackgroundResource(resourceTypeChat)

    }

    class ChatViewItemHolder : RecyclerView.ViewHolder {
        var binding: ChatViewItemBinding? = null

        constructor(itemView: View) : super(itemView) {
            binding = ChatViewItemBinding.bind(itemView)
        }

    }

    companion object {
        fun getReadableDateTimeNow(date: Date): String {
            return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }

        fun getReadableDateTimeWeek(date: Date): String {
            return SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        }

        fun getReadableDateTimeMonth(date: Date): String {
            return SimpleDateFormat("MMMM dd", Locale.getDefault()).format(date)
        }

        fun getReadableDateTimeOther(date: Date): String {
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        }
    }
}

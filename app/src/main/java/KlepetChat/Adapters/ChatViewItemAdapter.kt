package KlepetChat.Adapters

import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import android.util.Log
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

    lateinit var chatViewItems: MutableList<Chat>
    private val textSize = 20

    constructor(chatViewItems: MutableList<Chat>) : this() {
        this.chatViewItems = chatViewItems;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewItemHolder {
        var chatView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_view_item, parent,
                false)
        return ChatViewItemHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chatViewItems.size
    }

    override fun onBindViewHolder(holder: ChatViewItemHolder, position: Int) {
        val name = chatViewItems[position].name
        holder.binding?.textName?.text =
            if (name.length > textSize - 2) "${name.substring(0, textSize - 2)}..." else name
        holder.binding?.textDesc?.text = String()
        if (!chatViewItems[position].lastMessage.isNullOrBlank()) {
            var lastMessage = chatViewItems[position].lastMessage ?: " "
            holder.binding?.textDesc?.text = if (lastMessage.length > textSize + 5) "${
                lastMessage.substring(0, textSize + 5)}..." else lastMessage
            var date = chatViewItems[position].lastDate
            var dateLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            var period: Period = Period.between(dateLocal, LocalDate.now())
            Log.d("Period", "${period.days}, ${period.months}, ${period.years}")
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
        if (!chatViewItems[position].photo.isNullOrBlank()) {
            Picasso.get()
                .load(chatViewItems[position].photo)
                .placeholder(R.drawable.ic_chat_user)
                .error(R.drawable.ic_chat_user)
                .into(holder.binding?.imageChat)
        }
        var resourceTypeChat =
            when (chatViewItems[position].chatType) {
                ChatTypes.Favorites -> {
                    holder.binding?.imageChat?.setImageResource(R.drawable.ic_favorite)
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

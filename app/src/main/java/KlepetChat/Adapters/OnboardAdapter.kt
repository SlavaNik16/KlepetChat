package KlepetChat.Adapters

import KlepetChat.Activities.Fragments.OnboardingFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.klepetchat.R

class OnboardAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {

    private val titles = arrayOf("Klepet chat", "Приятный интерфейс", "Найди друзей и общайся!")

    private val descriptions = arrayOf("Добро пожаловать!",
        "Интуитивно понятное перемещение по приложению которое будет радовать глаза!",
        "Здесь вы можете общаться на любые темы с друзьями, или найти их тут!")

    private val images = arrayOf(R.drawable.ic_logo, R.drawable.ic_ui_design, R.drawable.ic_onbora_chat)
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.newInstance(
            titles[position],
            descriptions[position],
            position,
            images[position]
        )
    }

}
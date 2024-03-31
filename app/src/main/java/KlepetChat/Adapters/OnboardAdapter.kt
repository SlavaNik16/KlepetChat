package KlepetChat.Adapters

import KlepetChat.Activities.Fragments.OnboardingFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.klepetchat.R

class OnboardAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {

    private val titles = arrayOf("Добро пожаловать!", "Удобный поиск", "Любой маршрут на ваш вкус")

    private val descriptions = arrayOf("Сервис совместных поездок",
        "Находите попутчиков или водителей через поисковик",
        "Создавайте запрос, если результаты поиска не удовлетворяет")

    private val images = arrayOf(R.drawable.baseline_directions_car_24, R.drawable.ic_search, R.drawable.ic_menu)
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
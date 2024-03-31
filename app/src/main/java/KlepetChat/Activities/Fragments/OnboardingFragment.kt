package KlepetChat.Activities.Fragments

import KlepetChat.WebApi.Implementations.ViewModels.OnboardingViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.klepetchat.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var binding: FragmentOnboardingBinding? = null
    private val onboardingViewModel:OnboardingViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater)
        return binding!!.root
    }
    override fun onResume() {
        super.onResume()
        onboardingViewModel.position.value =  arguments?.getInt(ARG_POSITION)
    }
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_POSITION = "position"
        private const val ARG_IMAGE = "image"
        @JvmStatic
        fun newInstance(title: String, description: String, position: Int, image: Int) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                    putInt(ARG_POSITION, position)
                    putInt(ARG_IMAGE, image)
                }
            }
    }
}
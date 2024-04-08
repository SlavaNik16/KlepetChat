package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.OnboardAdapter
import KlepetChat.WebApi.Implementations.ViewModels.OnboardingViewModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityIntroBinding
import com.example.klepetchat.databinding.ActivityIntroNotificationPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {
    private var binding: ActivityIntroBinding? = null
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    private var buttonsChecked: Array<ImageView>? = null
    private var adapter: OnboardAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setListener()
        setObserve()
        onboarding()
    }

    private fun setListener() {
        binding?.textViewSkip?.setOnClickListener { onSkip() }
    }

    private fun setObserve() {
        onboardingViewModel.position.observe(this) { onboardingView(it) }
    }
    private fun removeListener() {
        binding?.textViewSkip?.setOnClickListener(null)
    }
    private fun removeComponent() {
        adapter = null
        buttonsChecked = null
        binding?.viewPager?.adapter = null
    }
    override fun onDestroy() {
        super.onDestroy()
        removeListener()
        removeComponent()
        viewModelStore.clear()
        binding = null
    }

    private fun onboardingView(position: Int) {
        if (buttonsChecked == null) {
            return
        }
        buttonsChecked?.forEach {
            it.setImageDrawable(
                getDrawable(R.drawable.baseline_radio_button_unchecked_24))
        }
        buttonsChecked?.get(position)?.setImageDrawable(
                getDrawable(R.drawable.baseline_radio_button_checked_24))
        if(adapter == null){
            return
        }
        if(position == adapter!!.itemCount - 1){
            binding?.textViewSkip?.text = "Завершить"
        }else{
            binding?.textViewSkip?.text = "Пропустить"
        }
    }

    private fun onboarding() {
        adapter = OnboardAdapter(this)
        binding?.viewPager?.adapter = adapter

        buttonsChecked =
            arrayOf(
                binding!!.butCheckOne,
                binding!!.butCheckTwo,
                binding!!.butCheckThree
            )
    }

    private fun onSkip() {
        if(!checkPermission()) {
            notificationPage()
            return
        }
        navigateToAuthorization()
    }

    private fun notificationPage() {
        val view =
            LayoutInflater.from(this)
                .inflate(R.layout.activity_intro_notification_page, null)
        val bindingNotification = ActivityIntroNotificationPageBinding.bind(view)
        setContentView(bindingNotification.root)

        bindingNotification.butOn.setOnClickListener {
            addPermission()
        }
        bindingNotification.butOff.setOnClickListener {
            navigateToAuthorization()
        }
    }
    private fun addPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                Manifest.permission.POST_NOTIFICATIONS
            ),
            Constants.REQUEST_PERMISSION_POST_NOTIFICATION
        )
    }
    private fun checkPermission():Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_PERMISSION_POST_NOTIFICATION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this@IntroActivity, "Уведомления включены!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@IntroActivity, "Уведомления отключены!", Toast.LENGTH_SHORT)
                        .show()
                }
                navigateToAuthorization()
            }
        }
    }
    private fun navigateToAuthorization(){
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
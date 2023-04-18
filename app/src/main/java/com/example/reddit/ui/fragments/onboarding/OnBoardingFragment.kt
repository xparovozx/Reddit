package com.example.reddit.ui.fragments.onboarding

import android.os.Bundle
import android.view.View
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.R
import com.example.reddit.databinding.FragmentOnboardingBinding
import com.example.reddit.utils.navigateWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {
    private val binding by viewBinding(FragmentOnboardingBinding::bind)

    @Inject
    lateinit var prefs: DataStore<Preferences>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screens = listOf(
            OnBoardingScreen(
                title = resources.getString(R.string.onboarding_screen_headline_1),
                description = resources.getString(R.string.onboarding_screen_txt_1),
                icon = R.drawable.ic_onboarding_1
            ),
            OnBoardingScreen(
                title = resources.getString(R.string.onboarding_screen_headline_2),
                description = resources.getString(R.string.onboarding_screen_txt_2),
                icon = R.drawable.ic_onboarding_2
            ),
            OnBoardingScreen(
                title = resources.getString(R.string.onboarding_screen_headline_3),
                description = resources.getString(R.string.onboarding_screen_headline_3),
                icon = R.drawable.ic_onboarding_3
            )
        )
        val onBoardingAdapter = ScreenAdapter(screens)
        binding.viewPager.adapter = onBoardingAdapter
        binding.indicator.setViewPager(binding.viewPager)
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                      if (position == onBoardingAdapter.itemCount - 1) {
                        val navOptions = navigateWithAnimation()
                        with(binding) {
                            buttonNext.text =resources.getString(R.string.onboarding_screen_proceed_btn)
                            buttonNext.setOnClickListener {
                                lifecycleScope.launch {
                                    saveOnboarding()
                                }
                                findNavController()
                                    .navigate(OnBoardingFragmentDirections.actionOnBoardingFragmentToLoginFragment(), navOptions)
                            }
                        }
                    } else {
                        with(binding) {
                            buttonNext.text = resources.getString(R.string.onboarding_screen_next_btn)
                            buttonNext.setOnClickListener {
                                viewPager.currentItem.let {
                                    viewPager.setCurrentItem(it + 1, false)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    suspend fun saveOnboarding() {
        prefs.edit {
            val oneTime = true
            it[preferencesKey<Boolean>("onBoard")] = oneTime
        }
    }
}
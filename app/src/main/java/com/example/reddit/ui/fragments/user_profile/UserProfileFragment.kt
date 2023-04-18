package com.example.reddit.ui.fragments.user_profile

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.reddit.R
import com.example.reddit.di.DarkLightPrefs
import com.example.reddit.databinding.FragmentUserProfileBinding
import com.example.reddit.utils.Constants.DARK_THEME_CHOICE
import com.example.reddit.utils.Constants.LIGHT_THEME_CHOICE
import com.example.reddit.utils.avatarConvert
import com.example.reddit.utils.navigateWithAnimation
import com.example.reddit.utils.showAlertDialog
import com.example.reddit.utils.showExitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    @Inject
    lateinit var darkPrefs: DarkLightPrefs
    private val binding by viewBinding(FragmentUserProfileBinding::bind)
    private val viewModel: UserProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchAppTheme()
        viewModel.getUserIdentity()
        binding.toFriendsBtn.setOnClickListener {
            val navOptions = navigateWithAnimation()
            val action =
                UserProfileFragmentDirections.actionUserProfileFragmentToFriendsListFragment()
            findNavController().navigate(action, navOptions)
        }
        binding.clearSavedBtn.setOnClickListener {
            val positiveListener = DialogInterface.OnClickListener { _, _ ->
                lifecycleScope.launch {
                    viewModel.unsaveAllCommentsFromUser()
                }
            }
            showAlertDialog(
                positiveListener = positiveListener,
                message = R.string.snackbar_unsave_comments,
                positiveBtn = R.string.snackbar_action_exit,
                negativeBtn = R.string.snackbar_action_noexit
            )
        }

        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            with(binding) {
                val userAvatar = user.avatarImage.avatarConvert()
                authorName.text = user.name
                authorReddit.text = user.subreddit.displayName
                Glide.with(iconUser)
                    .load(userAvatar)
                    .error(R.drawable.ic_redditor_default)
                    .centerInside()
                    .circleCrop()
                    .into(iconUser)
                viewModel.getSubsQuantity(userName = user.name)
                viewModel.getCommentsQuantity(userName = user.name)
            }
        }
        viewModel.userSubsQty.observe(viewLifecycleOwner) {
            binding.subredditsQty.text = "Сабреддитов  : $it"
        }
        viewModel.userCommentsQty.observe(viewLifecycleOwner) {
            binding.commentsQty.text = "Комментариев : $it"
        }
        binding.exitBtn.setOnClickListener {
            showExitDialog()
        }
    }

    private fun switchAppTheme() {
        binding.changeThemeBtn.setOnClickListener {
            when (darkPrefs.getDarkThemeStatus()) {
                LIGHT_THEME_CHOICE -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    (activity as AppCompatActivity).delegate.applyDayNight()
                    darkPrefs.setDarkThemeStatus(  DARK_THEME_CHOICE)
                }
                DARK_THEME_CHOICE -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    (activity as AppCompatActivity).delegate.applyDayNight()
                    darkPrefs.setDarkThemeStatus(  LIGHT_THEME_CHOICE)
                }
            }
        }
    }
}
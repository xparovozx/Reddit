package com.example.reddit.ui.fragments

import android.os.Handler
import android.os.Looper
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.preferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.reddit.R
import com.example.reddit.utils.navigateWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HelloFragment : Fragment(R.layout.fragment_hello) {

    @Inject
    lateinit var prefs: DataStore<Preferences>
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var  runnable : Runnable

    override fun onResume() {
        super.onResume()
        Timber.tag("Lifecycle").d("HelloFragment OnResume")
        runnable = Runnable {
            lifecycleScope.launch {
                prefs.data.collectLatest {
                    val navOptions = navigateWithAnimation()
                    if (it[preferencesKey<Boolean>("onBoard")] == true) {
                        view?.findNavController()?.navigate(HelloFragmentDirections.actionHelloFragmentToLoginFragment(), navOptions)
                    } else {
                        view?.findNavController()?.navigate(HelloFragmentDirections.actionHelloFragmentToOnBoardingFragment(), navOptions)
                    }
                }
            }
        }
        handler.postDelayed(runnable,3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}
package com.example.reddit.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkState @Inject constructor( private val context: Context ) {

    private val connectivityFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    @ExperimentalCoroutinesApi
    fun changes() = callbackFlow<Boolean> {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(isInternetAvailable(context))
            }
        }

        context.registerReceiver(broadcastReceiver, connectivityFilter)
        awaitClose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val result : Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }
}
package com.example.reddit.ui.fragments.user_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reddit.data.User
import com.example.reddit.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {
    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
        get() = _userLiveData
    private val _userSubsQty = MutableLiveData<Int>()
    val userSubsQty: LiveData<Int>
        get() = _userSubsQty
    private val _userCommentsQty = MutableLiveData<Int>()
    val userCommentsQty: LiveData<Int>
        get() = _userCommentsQty
    fun getUserIdentity() {
        viewModelScope.launch {
            try {
                val user = userRepo.getUserIdentity()
                _userLiveData.postValue(user)
            } catch (t: Throwable) {
                Timber.tag("GetUserIdentityError").d("User Identity error = $t")
            }
        }
    }

    fun getSubsQuantity(userName: String) {
        viewModelScope.launch {
            try {
                val subsQty = userRepo.getUserSubs(userName)
                _userSubsQty.postValue(subsQty)
            } catch (t: Throwable) {
                Timber.tag("GetUserIdentityError").d("User Subs error = $t")
            }
        }
    }

    fun getCommentsQuantity(userName: String) {
        viewModelScope.launch {
            try {
                val subsQty = userRepo.getUserCommentsSize(userName)
                _userCommentsQty.postValue(subsQty)
            } catch (t: Throwable) {
                Timber.tag("GetUserIdentityError").d("User Subs error = $t")
            }
        }
    }

    suspend fun unsaveAllCommentsFromUser() {
        viewModelScope.launch {
            try {
                userRepo.unSaveAllCommentsForUser()
                userRepo.unSaveSubForUser()
            } catch (t: Throwable) {
                Timber.tag("SaveCommentError").d("Comment unsave error = $t")
            }
        }
    }
}
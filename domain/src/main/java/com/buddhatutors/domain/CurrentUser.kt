package com.buddhatutors.domain

import com.buddhatutors.domain.model.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
object CurrentUser {

    // Observable user session data using StateFlow
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    var accessToken: String? = null

    private val isLoggedIn: Boolean get() = _user.value != null

    fun initialize(userSessionDataSource: UserSessionDataSource) {
        CoroutineScope(Dispatchers.Main).launch {
            userSessionDataSource.getUserSession()
                .collect { _user.value = it }
        }
    }

}
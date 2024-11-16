package com.buddhatutors.domain

import com.buddhatutors.domain.model.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
object CurrentUser {

    // Observable user session data using StateFlow
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val isLoggedIn: Boolean get() = _user.value != null

    private val job: Job? = null

    private var isInitialize = false

    fun initialize(userSessionDataSource: UserSessionDataSource) {
        if (!isInitialize) {
            isInitialize = true
            CoroutineScope(Dispatchers.Main).launch {
                userSessionDataSource.getUserSession()
                    .collect { _user.value = it }
            }
        }
    }

    fun dispose() {
        isInitialize = false
        job?.cancel()
    }
}
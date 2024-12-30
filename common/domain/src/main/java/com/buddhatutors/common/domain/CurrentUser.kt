package com.buddhatutors.common.domain

import com.buddhatutors.common.domain.model.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object CurrentUser {

    // Observable user session data using StateFlow
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val isLoggedIn: Boolean get() = _user.value != null

    private val job: Job? = null

    private var isInitialize = false

    fun setUser(user: User?) {
        _user.value = user
    }

    fun dispose() {
        isInitialize = false
        job?.cancel()
    }
}
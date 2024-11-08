package com.buddhatutors.common.auth

import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.model.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val userSessionDataSource: UserSessionDataSource
) {

    companion object {

        private var _user: User? = null
        val user: User? get() = _user

        private val isLoggedIn: Boolean get() = _user != null

    }


    suspend fun getUser(): User? {
        return userSessionDataSource.getAuthToken().firstOrNull()
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            userSessionDataSource.getAuthToken()
                .onEach { _user = it }.launchIn(this)
        }
    }

}
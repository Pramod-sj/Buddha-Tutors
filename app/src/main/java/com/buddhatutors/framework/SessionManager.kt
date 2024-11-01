package com.buddhatutors.framework

import com.buddhatutors.domain.PreferenceManager
import com.buddhatutors.domain.model.User
import com.buddhatutors.framework.data.model.UserEntity
import com.buddhatutors.framework.data.model.toDomain
import com.buddhatutors.framework.data.model.toEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class SessionManager @Inject constructor(
    private val preferencesManager: PreferenceManager<String>
) {

    companion object {

        private const val USER_PREF_KEY = "user"

        private var _user: User? = null
        val user: User? get() = _user

        val isLoggedIn: Boolean
            get() = FirebaseAuth.getInstance().currentUser != null

    }

    private val gson = Gson()

    suspend fun setUser(user: User) {
        preferencesManager.set(USER_PREF_KEY, gson.toJson(user.toEntity()))
    }

    suspend fun clearUser() {
        preferencesManager.remove(USER_PREF_KEY)
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            preferencesManager.get(USER_PREF_KEY)
                .onEach {
                    _user = gson.fromJson(it, UserEntity::class.java)?.toDomain()
                }.launchIn(this)
        }
    }

}
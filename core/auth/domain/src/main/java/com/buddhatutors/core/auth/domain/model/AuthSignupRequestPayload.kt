package com.buddhatutors.core.auth.domain.model

import com.buddhatutors.model.user.User

sealed class AuthSignupRequestPayload {

    abstract val user: User

    data class EmailPasswordAuthSignupRequestPayload(
        override val user: User,
        val password: String,
    ) : AuthSignupRequestPayload()

}

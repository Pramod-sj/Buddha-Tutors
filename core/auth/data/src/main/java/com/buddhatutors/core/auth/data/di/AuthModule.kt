package com.buddhatutors.core.auth.data.di

import com.buddhatutors.core.auth.data.EmailPasswordLoginHandler
import com.buddhatutors.core.auth.data.EmailPasswordSignupHandler
import com.buddhatutors.core.auth.data.FirebaseEmailVerificationService
import com.buddhatutors.core.auth.data.FirebaseForgotPasswordServiceImpl
import com.buddhatutors.core.auth.data.FirebaseGoogleLoginHandler
import com.buddhatutors.core.auth.domain.EMAIL_SIGN_IN_METHOD_NAME
import com.buddhatutors.core.auth.domain.EMAIL_SIGN_UP_METHOD_NAME
import com.buddhatutors.core.auth.domain.EmailVerificationService
import com.buddhatutors.core.auth.domain.ForgotPasswordService
import com.buddhatutors.core.auth.domain.GOOGLE_SIGN_IN_METHOD_NAME
import com.buddhatutors.core.auth.domain.LoginHandler
import com.buddhatutors.core.auth.domain.SignupHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthModule {

    @Binds
    @IntoMap
    @StringKey(GOOGLE_SIGN_IN_METHOD_NAME)
    fun bindGoogleLoginHandler(firebaseGoogleLoginHandler: FirebaseGoogleLoginHandler): LoginHandler

    @Binds
    @IntoMap
    @StringKey(EMAIL_SIGN_IN_METHOD_NAME)
    fun bindEmailPasswordLoginHandler(emailPasswordLoginHandler: EmailPasswordLoginHandler): LoginHandler

    @Binds
    @IntoMap
    @StringKey(EMAIL_SIGN_UP_METHOD_NAME)
    fun bindEmailPasswordSignupHandler(emailPasswordSignupHandler: EmailPasswordSignupHandler): SignupHandler

    @Binds
    fun bindEmailVerificationService(firebaseEmailVerificationService: FirebaseEmailVerificationService): EmailVerificationService

    @Binds
    fun bindForgotPasswordService(firebaseForgotPasswordServiceImpl: FirebaseForgotPasswordServiceImpl): ForgotPasswordService
}
package com.buddhatutors.auth.di

import com.buddhatutors.auth.data.EmailPasswordLoginHandler
import com.buddhatutors.auth.data.EmailPasswordSignupHandler
import com.buddhatutors.auth.data.FirebaseEmailVerificationService
import com.buddhatutors.auth.data.FirebaseForgotPasswordServiceImpl
import com.buddhatutors.auth.data.FirebaseGoogleLoginHandler
import com.buddhatutors.auth.domain.EmailVerificationService
import com.buddhatutors.auth.domain.ForgotPasswordService
import com.buddhatutors.auth.domain.LoginHandler
import com.buddhatutors.auth.domain.SignupHandler
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
    @StringKey(com.buddhatutors.auth.domain.GOOGLE_SIGN_IN_METHOD_NAME)
    fun bindGoogleLoginHandler(firebaseGoogleLoginHandler: FirebaseGoogleLoginHandler): LoginHandler

    @Binds
    @IntoMap
    @StringKey(com.buddhatutors.auth.domain.EMAIL_SIGN_IN_METHOD_NAME)
    fun bindEmailPasswordLoginHandler(emailPasswordLoginHandler: EmailPasswordLoginHandler): LoginHandler

    @Binds
    @IntoMap
    @StringKey(com.buddhatutors.auth.domain.EMAIL_SIGN_UP_METHOD_NAME)
    fun bindEmailPasswordSignupHandler(emailPasswordSignupHandler: EmailPasswordSignupHandler): SignupHandler

    @Binds
    fun bindEmailVerificationService(firebaseEmailVerificationService: FirebaseEmailVerificationService): EmailVerificationService

    @Binds
    fun bindForgotPasswordService(firebaseForgotPasswordServiceImpl: FirebaseForgotPasswordServiceImpl): ForgotPasswordService
}
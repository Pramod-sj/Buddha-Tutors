package com.buddhatutors.di

import com.buddhatutors.auth.EmailPasswordLoginHandler
import com.buddhatutors.auth.EmailPasswordSignupHandler
import com.buddhatutors.auth.GoogleLoginHandler
import com.buddhatutors.domain.EMAIL_SIGN_IN_METHOD_NAME
import com.buddhatutors.domain.EMAIL_SIGN_UP_METHOD_NAME
import com.buddhatutors.domain.GOOGLE_SIGN_IN_METHOD_NAME
import com.buddhatutors.domain.LoginHandler
import com.buddhatutors.domain.SignupHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    @IntoMap
    @StringKey(GOOGLE_SIGN_IN_METHOD_NAME)
    fun bindGoogleLoginHandler(googleLoginHandler: GoogleLoginHandler): LoginHandler

    @Binds
    @IntoMap
    @StringKey(EMAIL_SIGN_IN_METHOD_NAME)
    fun bindEmailPasswordLoginHandler(emailPasswordLoginHandler: EmailPasswordLoginHandler): LoginHandler

    @Binds
    @IntoMap
    @StringKey(EMAIL_SIGN_UP_METHOD_NAME)
    fun bindEmailPasswordSignupHandler(emailPasswordSignupHandler: EmailPasswordSignupHandler): SignupHandler

}
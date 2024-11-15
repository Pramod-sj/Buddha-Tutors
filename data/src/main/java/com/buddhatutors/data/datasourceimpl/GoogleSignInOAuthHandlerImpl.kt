package com.buddhatutors.data.datasourceimpl

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.buddhatutors.domain.ContextWrapper
import com.buddhatutors.domain.KEY_ID_TOKEN
import com.buddhatutors.domain.OAuthHandler
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.model.Resource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class ActivityContextWrapper(val context: Context) : ContextWrapper

fun ContextWrapper.getContext() = (this as? ActivityContextWrapper)?.context


internal class GoogleSignInOAuthHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSessionDataSource: UserSessionDataSource
) : OAuthHandler {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        const val SERVER_CLIENT_ID =
            "939778233487-eoh0f5o5n53k3le4u6447ed600eabpr4.apps.googleusercontent.com"
    }

    override suspend fun authenticate(contextWrapper: ContextWrapper): Resource<String> {

        val existingIdToken = userSessionDataSource.getUserToken(KEY_ID_TOKEN)

        if (existingIdToken != null && !isTokenExpired(existingIdToken)) {
            return Resource.Success(existingIdToken)
        }

        val activityContextWrapper = contextWrapper.getContext()
            ?: return Resource.Error(Throwable("Context provided is not a activity context"))

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setAutoSelectEnabled(true)
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(SERVER_CLIENT_ID)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {

            val result = credentialManager.getCredential(
                request = request,
                context = activityContextWrapper,
            )

            // Handle the successfully returned credential.
            when (val credential = result.credential) {

                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            Log.i("idToken:", googleIdTokenCredential.idToken)
                            Resource.Success(googleIdTokenCredential.idToken).also {
                                userSessionDataSource.saveUserTokens(map = mapOf(KEY_ID_TOKEN to it.data))
                            }
                        } catch (e: GoogleIdTokenParsingException) {
                            Resource.Error(Throwable("Received an invalid google id token response: " + e.message))
                        }
                    } else {
                        // Catch any unrecognized custom credential type here.
                        Resource.Error(Throwable("Unexpected type of credential"))
                    }
                }

                else -> {
                    // Catch any unrecognized credential type here.
                    Resource.Error(Throwable("Unexpected type of credential"))
                }
            }

        } catch (e: Exception) {
            Resource.Error(Throwable("Unexpected type of credential: " + e.message))
        }
    }

}
package com.buddhatutors.core.auth.google_oauth.oauth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.buddhatutors.core.auth.domain.UserSessionPreference
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.model.Resource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class GoogleSignInOAuthHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSessionDataSource: com.buddhatutors.core.auth.domain.UserSessionPreference
) : OAuthHandler {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        const val SERVER_CLIENT_ID =
            "63972000114-53ldpb2ddg300nk80lj5hmo98r289oun.apps.googleusercontent.com"
    }

    override suspend fun authenticate(activity: Activity): Resource<String> {

        val existingIdToken = userSessionDataSource.getUserToken(com.buddhatutors.core.auth.domain.UserSessionPreference.KEY_ID_TOKEN)

        if (existingIdToken != null) {
            return Resource.Success(existingIdToken)
        }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setAutoSelectEnabled(false)
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SERVER_CLIENT_ID)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {

            val result = credentialManager.getCredential(
                request = request,
                context = activity,
            )

            // Handle the successfully returned credential.
            when (val credential = result.credential) {

                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)

                            if (googleIdTokenCredential.id == CurrentUser.user.value?.email) {
                                Log.i("idToken:", googleIdTokenCredential.idToken)
                                Resource.Success(googleIdTokenCredential.idToken).also {
                                    userSessionDataSource.saveUserTokens(
                                        map = mapOf(
                                            com.buddhatutors.core.auth.domain.UserSessionPreference.KEY_ID_TOKEN to it.data
                                        )
                                    )
                                }
                            } else {
                                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                                Resource.Error(Throwable("Please select the email id you used to login"))
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
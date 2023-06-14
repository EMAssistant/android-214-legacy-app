package com.randomvoids.emassistant.view.ui.profile.login

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.User
import com.randomvoids.emassistant.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    var activity: Activity? = null
    private val fetchedUserLiveData : MutableLiveData<User> = MutableLiveData()
    val userLiveData : MutableLiveData<User>
    val client = OkHttpClient()
    var microsoftAccount: MutableLiveData<IAccount> = MutableLiveData()

    // See: https://docs.microsoft.com/en-us/graph/deployments#microsoft-graph-and-graph-explorer-service-root-endpoints
    val MS_GRAPH_ROOT_ENDPOINT = "https://graph.microsoft.com/" + "v1.0/me"

    init {
        userLiveData = fetchedUserLiveData.switchMap {
            launchOnViewModelScope {
                Timber.d("in launchonviewmodelscope")
                this.userProfileRepository.fetch(1).asLiveData()
            }
        } as MutableLiveData<User>
        fetchedUserLiveData.postValue((launchOnViewModelScope {
            this.userProfileRepository.fetch(1).asLiveData()
        }).value)
    }

    fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Timber.d("thing: %s %s ", account!!.email, account.displayName)
            GlobalScope.launch {
                val user = userProfileRepository.getUserById(1)!!
                if(user.firstName=="New") {
                    account.givenName?.let { user.firstName = it }
                    account.familyName?.let { user.lastName = it }
                        userProfileRepository.update(user)
                        Timber.d("updated")
                }
            }
            val idToken = account.idToken
            Timber.d("token: $idToken")
            // TODO(developer): send ID Token to server and validate
            //updateUI(account)
        } catch (e: ApiException) {
            Timber.w(e, "handleSignInResult:error%s", e.message)
            //updateUI(null)
        }
    }

    fun microsoftOauth2SignIn(view: View) {
        mSingleAccountApp!!.getCurrentAccountAsync(object :
            ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                Timber.d("account loaded: " + activeAccount!!.id + " token: " + activeAccount!!.idToken + " " + activeAccount.authority)
                Timber.d("name: " + activeAccount!!)
                microsoftAccount.value = activeAccount!!
                //updateUI(activeAccount)
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    //performOperationOnSignOut()
                }
            }

            override fun onError(exception: MsalException) {
                Timber.d(exception.toString())
            }
        })


        mSingleAccountApp!!.signIn(activity!!,
            null, Array(1) { "user.read" },
            getAuthInteractiveCallback())
        Timber.d("signin called")

    }
    private fun getAuthInteractiveCallback(): AuthenticationCallback {
        Timber.d("creating auth ballback")
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Timber.d("Successfully authenticated")

                Timber.d(
                    "ID Token: %s", authenticationResult.account.claims!!["id_token"]
                )
                Timber.d("access token: %s", authenticationResult.accessToken)
                Timber.d("account: %s",authenticationResult.account.toString())

                /* Update account */
                microsoftAccount.value = authenticationResult.account
                //updateUI()

                /* call graph */callGraphAPI(authenticationResult.accessToken)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Timber.d(
                    "Authentication failed: $exception"
                )
                //displayError(exception)
                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            override fun onCancel() {
                /* User canceled the authentication */
                Timber.d("User cancelled login.")
            }
        }
    }

    /**
     * Make an HTTP request to obtain MSGraph data
     */
    private fun callGraphAPI(accessToken: String) {
        val url = HttpUrl.parse(MS_GRAPH_ROOT_ENDPOINT)!!.newBuilder()
            .addQueryParameter("key", "value")
            .build().toString()

        val request = Request.Builder()
            .header("Authorization", accessToken)
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Timber.d("Error: %s", e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    Timber.d("Response: $response")
                    println(response.body().toString())
                }
            }
        })
    }
}
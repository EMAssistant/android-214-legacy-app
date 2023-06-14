package com.randomvoids.emassistant.view.ui.profile.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.FragmentPersonalProfileLoginBinding
import com.randomvoids.emassistant.util.ActivityRequestCodes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

//TODO
// https://developers.google.com/identity/sign-in/android/sign-in
// https://developers.google.com/identity/sign-in/android/backend-auth
@AndroidEntryPoint
class LoginFragment : Fragment() {
    @VisibleForTesting val loginViewModel: LoginViewModel by viewModels()
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .requestIdToken("124595023151-b2l70ofel5t58soqtq5hh6juptrrc660.apps.googleusercontent.com") //id for backend
        .build()

    /* Azure AD v2 Configs */
    private val AUTHORITY = "https://login.microsoftonline.com/common"

    /* Azure AD Variables */
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        PublicClientApplication.createSingleAccountPublicClientApplication(
            requireContext(),
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    /**
                     * This test app assumes that the app is only going to support one account.
                     * This requires "account_mode" : "SINGLE" in the config json file.
                     *
                     */
                    mSingleAccountApp = application

                    loginViewModel.mSingleAccountApp = application
                }
                override fun onError(exception: MsalException) {
                    Timber.d(exception.toString())
                }
            }
        )


        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        /*GoogleSignIn.getClient(requireActivity(), gso).silentSignIn()
            .addOnCompleteListener(
                requireActivity(),
                OnCompleteListener<GoogleSignInAccount?> { task -> Timber.d("log: " + task.getResult(ApiException::class.java)!!.idToken) })*/
        val binding = FragmentPersonalProfileLoginBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@LoginFragment
            googleSigninButton.setOnClickListener {
                val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, ActivityRequestCodes.GOOGLE_OAUTH_LOGIN)
            }
            viewModel = loginViewModel.apply {
                this.mGoogleSignInClient = mGoogleSignInClient
                this.activity = requireActivity()
            }
        }
        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ActivityRequestCodes.GOOGLE_OAUTH_LOGIN -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    loginViewModel.handleGoogleSignInResult(task);
                } catch (e: ApiException) {
                    //this happens if for some reason the user decides not to login to google
                    //TODO do we want to do anything here?
                }
            }
            else -> {
                Timber.d("code: $requestCode")
            }
        }
    }
}
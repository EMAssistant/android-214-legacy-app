package com.randomvoids.emassistant.view.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.FragmentPersonalProfileViewBinding
import com.randomvoids.emassistant.util.RequestCodes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class PersonalProfileFragment : Fragment() {
    private lateinit var signatureImageView : ImageView
    @VisibleForTesting val userProfileViewModel : UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return FragmentPersonalProfileViewBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@PersonalProfileFragment

            signatureImageView = this.signatureImageViewer
            updateSignatureImageView()
            user = userProfileViewModel.apply {
                seed()
                fetchUserProfile()
            }
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //TODO reenable
        //requireActivity().menuInflater.inflate(R.menu.menu_profile_view, menu)
        requireActivity().menuInflater.inflate(R.menu.menu_with_edit, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.edit_profile_button -> {
                findNavController().navigate(ProfilePagerContainerFragmentDirections.actionNavigateToPersonalProfileEditor())
                true
            }
            R.id.login_button -> {
                findNavController().navigate(R.id.action_navigate_to_personal_profile_login)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("request code: " + requestCode + ", resultCode: " + resultCode + "Data: " + data.toString())
        if(requestCode == RequestCodes.HUMAN_SIGNATURE_CREATION) {
            updateSignatureImageView()
        }
    }

    private fun updateSignatureImageView() {
        val signatureFile = File(requireContext().filesDir, "humanSignature.png")
        if(signatureFile.exists()) {
            signatureImageView.setImageBitmap(BitmapFactory.decodeFile(signatureFile.absolutePath))
        }
    }
}
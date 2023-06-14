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
import com.randomvoids.emassistant.HumanSignatureCollectorActivity
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.FragmentPersonalProfileEditBinding
import com.randomvoids.emassistant.util.RequestCodes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class PersonalProfileEditFragment : Fragment() {
    private lateinit var signatureImageView : ImageView
    @VisibleForTesting val userProfileViewModel : UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentPersonalProfileEditBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@PersonalProfileEditFragment

            getHumanSignature.setOnClickListener {
                val intent = Intent(requireContext(), HumanSignatureCollectorActivity::class.java)
                startActivityForResult(intent, RequestCodes.HUMAN_SIGNATURE_CREATION)
            }
            signatureImageView = this.signatureImageViewer
            updateSignatureImageView()
            user = userProfileViewModel.apply {
                fetchUserProfile()
            }
        }
       return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_with_save, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.save_button -> {
                userProfileViewModel.onClickSave()
                findNavController().navigateUp()
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
package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.Ics214FragmentDetailsViewBinding
import com.randomvoids.emassistant.util.RequestCodes
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ICS214DetailsViewFragment : Fragment() {
    @VisibleForTesting val ics214EditorViewModel: ICS214EditorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = Ics214FragmentDetailsViewBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ICS214DetailsViewFragment
            viewModel = ics214EditorViewModel

            exportToPdf.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startActivityForResult(intent, RequestCodes.PDF_EXPORT)
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_with_edit_delete, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.edit_profile_button -> {
                val action =
                    ICS214FormContainerFragmentDirections.actionNavigateToIcs214DetailsEditor(
                        ics214EditorViewModel.ics214LiveData.value!!.ics214Details.id,
                        ics214EditorViewModel.ics214LiveData.value!!.incident.incidentName
                    )
                findNavController().navigate(action)
                true
            }
            R.id.delete_button -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete ICS 214?")
                    .setMessage("Do you really want to delete this ICS 214 form?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            Toast.makeText(
                                requireContext(),
                                "Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            ics214EditorViewModel.deleteCurrentICS214()
                            findNavController().navigateUp()
                        })
                    .setNegativeButton(android.R.string.cancel, null).show()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.PDF_EXPORT -> {
                if (data == null)
                    return
                ics214EditorViewModel.exportICS214ToPDF(this.requireContext(), data.data!!)
            }
            else -> {
            }
        }
    }
}
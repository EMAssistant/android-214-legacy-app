package com.randomvoids.emassistant.view.ui.profile.mileagelog

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.PersonalSectionFragmentMileageLogBinding
import com.randomvoids.emassistant.view.adapter.MileageLogListItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MileageLogFragment : Fragment() {
    companion object {
        const val SELECT_CSV_EXPORT_PATH = 1
    }
    @VisibleForTesting val mileageLogViewModel: MileageLogViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return PersonalSectionFragmentMileageLogBinding.inflate(inflater, container, false).apply {
            mileageLogRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                viewModel = mileageLogViewModel
                adapter = MileageLogListItemAdapter(mileageLogViewModel).apply {
                    mileageLogViewModel.mileageLogListData.observe(viewLifecycleOwner, Observer { list -> submitList(list) })
                }
            }
            lifecycleOwner = this@MileageLogFragment
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_for_mileage_log, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_button -> {
                mileageLogViewModel.onNewMileageLogEntryButtonClick(requireView())
                true
            }
            R.id.export_button -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startActivityForResult(intent, SELECT_CSV_EXPORT_PATH)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("activity result returned")
        when (requestCode) {
            SELECT_CSV_EXPORT_PATH -> {
                data?.let {
                    mileageLogViewModel.exportToCSV(this.requireContext(), it.data!!)
                }
            }
            else -> {
                Timber.w("for some reason MileageLogFragment go an unrecognized request code")
                Timber.d("request code: " + requestCode + ", resultCode: " + resultCode + "Data: " + data.toString())
            }
        }
    }
}
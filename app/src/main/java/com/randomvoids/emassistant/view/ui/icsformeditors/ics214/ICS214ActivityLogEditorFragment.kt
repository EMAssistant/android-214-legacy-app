package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.randomvoids.emassistant.databinding.FragmentIcs214ActivityLogBinding
import com.randomvoids.emassistant.util.RequestCodes
import com.randomvoids.emassistant.view.adapter.ActivityLogEntriesListItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class ICS214ActivityLogEditorFragment : Fragment() {
    @VisibleForTesting val ics214EditorViewModel: ICS214EditorViewModel by activityViewModels()
    @VisibleForTesting val activityLogEditorViewModel : ActivityLogEditorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentIcs214ActivityLogBinding.inflate(inflater, container, false).apply {
            activityLogEntryListRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                viewModel = activityLogEditorViewModel
                adapter = ActivityLogEntriesListItemAdapter(activityLogEditorViewModel.apply { ics214EditorViewModel.ics214LiveData.value?.let { fetchActivityLog(it.ics214Details.id) } }).apply {
                    activityLogEditorViewModel.activityLogEntryListLiveData.observe(viewLifecycleOwner, Observer { list -> submitList(list) })
                }
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1234 -> {

            }
        }
    }
}
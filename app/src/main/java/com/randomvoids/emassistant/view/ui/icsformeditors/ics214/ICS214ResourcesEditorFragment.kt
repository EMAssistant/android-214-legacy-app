package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

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
import com.randomvoids.emassistant.databinding.Ics214FragmentResourcesListBinding
import com.randomvoids.emassistant.view.adapter.ICS214ResourcesListItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ICS214ResourcesEditorFragment : Fragment() {
    @VisibleForTesting val ics214EditorViewModel: ICS214EditorViewModel by activityViewModels()
    @VisibleForTesting val ics214ResourcesListViewModel : ICS214ResourcesListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = Ics214FragmentResourcesListBinding.inflate(inflater, container, false).apply {
            icsResourcesListRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                viewModel = ics214ResourcesListViewModel
                adapter = ICS214ResourcesListItemAdapter(ics214ResourcesListViewModel.apply { ics214EditorViewModel.ics214LiveData.value?.let { fetchICSResourceList(it.ics214Details.id) } }).apply {
                    ics214ResourcesListViewModel.resourcesListLiveData.observe(viewLifecycleOwner, Observer { list -> list?.let { submitList(it) }})
                }
            }
        }

        return binding.root
    }
}
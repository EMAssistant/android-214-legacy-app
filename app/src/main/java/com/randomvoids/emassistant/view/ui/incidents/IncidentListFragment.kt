package com.randomvoids.emassistant.view.ui.incidents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.randomvoids.emassistant.view.adapter.IncidentListItemAdapter
import com.randomvoids.emassistant.databinding.FragmentIncidentListBinding
import com.randomvoids.emassistant.model.incidents

class IncidentListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentIncidentListBinding.inflate(inflater, container, false).apply {
            /*incidentList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                adapter = IncidentListItemAdapter().apply {
                    submitList(incidents)
                }
            }*/
        }

        return binding.root
    }
}
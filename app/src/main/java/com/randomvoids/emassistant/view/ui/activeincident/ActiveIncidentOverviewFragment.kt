package com.randomvoids.emassistant.view.ui.activeincident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.randomvoids.emassistant.databinding.ActiveIncidentOverviewFragmentBinding

class ActiveIncidentOverviewFragment : Fragment() {
    @VisibleForTesting val activeIncidentViewModel: ActiveIncidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ActiveIncidentOverviewFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ActiveIncidentOverviewFragment
            viewModel = activeIncidentViewModel
            val items = listOf("GSAR", "Driving", "K9", "FAST/RBO")

        }.root
    }
}
package com.randomvoids.emassistant.view.ui.myforms

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.FragmentMyIcs214ListBinding
import com.randomvoids.emassistant.view.adapter.ICS214ListItemAdapter
import com.randomvoids.emassistant.util.RequestCodes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ICS214ListFragment : Fragment() {
    @VisibleForTesting val ics214ListViewModel: ICS214ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentMyIcs214ListBinding.inflate(inflater, container, false).apply {
            ics214List.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                viewModel = ics214ListViewModel.apply {
                    seed()
                }
                adapter = ICS214ListItemAdapter(ics214ListViewModel).apply {
                    ics214ListViewModel.ics214ListLiveData.observe(viewLifecycleOwner, Observer {list -> submitList(list) })
                    Timber.d("adapter initialized")
                }

            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_with_new, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_button -> {
                val newIncidentDialogFragment = NewICS214DialogFragment()
                newIncidentDialogFragment.setTargetFragment(this, 101)
                newIncidentDialogFragment.show(parentFragmentManager, "new_incident_dialog")
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("activity result returned")
        if(requestCode == RequestCodes.ICS214_OPERATIONAL_PERIOD_START_DATE_PICKER) {
            Timber.d(data!!.getSerializableExtra("selectedDate").toString())
        }  else {
            Timber.d("request code: " + requestCode + ", resultCode: " + resultCode + "Data: " + data.toString())

        }
    }
}
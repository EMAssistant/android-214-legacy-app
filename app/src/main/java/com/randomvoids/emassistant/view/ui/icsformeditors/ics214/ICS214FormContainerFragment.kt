package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.FragmentIcs214EditorViewContainerBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private val TABS = arrayOf("Activity Log", "Details", "Resources")

@AndroidEntryPoint
class ICS214FormContainerFragment : Fragment() {
    @VisibleForTesting val ics214EditorViewModel: ICS214EditorViewModel by activityViewModels()
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var ics214FormContainerAdapter: ICS214FormContainerAdapter
    private lateinit var viewPager: ViewPager2
    private val args by navArgs<ICS214FormContainerFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return FragmentIcs214EditorViewContainerBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ICS214FormContainerFragment
            ics214EditorViewModel.apply {
                fetchFullICS214(args.icS214Id)
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ics214FormContainerAdapter = ICS214FormContainerAdapter(this)
        viewPager = view.findViewById(R.id.viewpager_ics_214)
        viewPager.adapter = ics214FormContainerAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.tabs_ics_214)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = TABS[position]
        }.attach()
        viewPager.setCurrentItem(1, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_blank, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}

class ICS214FormContainerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TABS.size
    override fun createFragment(position: Int): Fragment {
        Timber.d("position: %s", position)
        return when(position) {
            0 -> {
                ICS214ActivityLogEditorFragment()
            }
            1 -> {
                ICS214DetailsViewFragment()
            }
            else -> {
                ICS214ResourcesEditorFragment()
            }
        }
    }
}


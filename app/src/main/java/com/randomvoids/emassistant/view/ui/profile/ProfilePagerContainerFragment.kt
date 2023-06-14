package com.randomvoids.emassistant.view.ui.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.PersonalSectionFragmentViewPagerContainerBinding
import com.randomvoids.emassistant.view.ui.profile.mileagelog.MileageLogFragment
import dagger.hilt.android.AndroidEntryPoint

private val TABS = arrayOf("Mileage Log", "Profile")

@AndroidEntryPoint
class ProfilePagerContainerFragment : Fragment() {
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return PersonalSectionFragmentViewPagerContainerBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ProfilePagerContainerFragment

        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewpager_personal_section)
        viewPager.adapter = PersonalSectionContainerAdapter(this)

        val tabLayout: TabLayout = view.findViewById(R.id.tabs_personal_section)
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

class PersonalSectionContainerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TABS.size
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                MileageLogFragment()
            }
            1 -> {
                PersonalProfileFragment()
            }
            else -> {
                PersonalProfileFragment()
            }
        }
    }
}
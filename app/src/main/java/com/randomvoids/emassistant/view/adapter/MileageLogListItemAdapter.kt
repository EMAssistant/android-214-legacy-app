package com.randomvoids.emassistant.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.randomvoids.emassistant.databinding.ActivityLogEntryBinding
import com.randomvoids.emassistant.databinding.ListItemMileageLogEntryBinding
import com.randomvoids.emassistant.model.ActivityLogEntry
import com.randomvoids.emassistant.model.MileageLogEntry
import com.randomvoids.emassistant.model.MileageLogEntryDiff
import com.randomvoids.emassistant.view.ui.icsformeditors.ics214.ActivityLogEditorViewModel
import com.randomvoids.emassistant.view.ui.profile.mileagelog.MileageLogViewModel

class MileageLogListItemAdapter (
    private val mileageLogViewModel: MileageLogViewModel
) : ListAdapter<MileageLogEntry, MileageLogListItemAdapter.MileageLogListItemViewHolder>(MileageLogEntryDiff) {
    private val mileageLogItems: MutableList<MileageLogEntry> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MileageLogListItemViewHolder {
        return MileageLogListItemViewHolder(
            ListItemMileageLogEntryBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun addList(mileageLog: List<MileageLogEntry>) {
        mileageLogItems.removeAll { true }
        mileageLogItems.addAll(mileageLog)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MileageLogListItemViewHolder, position: Int) {
        holder.bind(getItem(position), mileageLogViewModel)
    }
    class MileageLogListItemViewHolder(private val binding: ListItemMileageLogEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mileageLogEntry: MileageLogEntry, mileageLogViewModel: MileageLogViewModel) {
            binding.run {
                this.mileageLogEntry = mileageLogEntry
                this.viewModel = mileageLogViewModel
                executePendingBindings()
            }
        }
    }
}
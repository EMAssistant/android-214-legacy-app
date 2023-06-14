package com.randomvoids.emassistant.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.randomvoids.emassistant.databinding.ActivityLogEntryBinding
import com.randomvoids.emassistant.model.ActivityLogEntry
import com.randomvoids.emassistant.model.ActivityLogEntryDiff
import com.randomvoids.emassistant.view.ui.icsformeditors.ics214.ActivityLogEditorViewModel

class ActivityLogEntriesListItemAdapter (
    private val activityLogEditorViewModel: ActivityLogEditorViewModel
) : ListAdapter<ActivityLogEntry, ActivityLogEntriesListItemAdapter.ActivityLogEntryListItemViewHolder>(ActivityLogEntryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityLogEntryListItemViewHolder {
        return ActivityLogEntryListItemViewHolder(
            ActivityLogEntryBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ActivityLogEntryListItemViewHolder, position: Int) {
        holder.bind(getItem(position), activityLogEditorViewModel)
    }
    class ActivityLogEntryListItemViewHolder(private val binding: ActivityLogEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activityLogEntry: ActivityLogEntry, activityLogEditorViewModel: ActivityLogEditorViewModel) {
            binding.run {
                this.activityLogEntry = activityLogEntry
                this.viewModel = activityLogEditorViewModel
                executePendingBindings()
            }
        }
    }
}

package com.randomvoids.emassistant.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.randomvoids.emassistant.databinding.Ics214ListItemResourceBinding
import com.randomvoids.emassistant.model.ICSResource
import com.randomvoids.emassistant.model.ICSResourceDiff
import com.randomvoids.emassistant.view.ui.icsformeditors.ics214.ICS214EditorViewModel
import com.randomvoids.emassistant.view.ui.icsformeditors.ics214.ICS214ResourcesListViewModel

class ICS214ResourcesListItemAdapter (
    private val ics214ResourcesListViewModel : ICS214ResourcesListViewModel
) : ListAdapter<ICSResource, ICS214ResourcesListItemAdapter.ICSResourcesListItemViewHolder>(ICSResourceDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ICSResourcesListItemViewHolder {
        return ICSResourcesListItemViewHolder(
            Ics214ListItemResourceBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ICSResourcesListItemViewHolder, position: Int) {
        holder.bind(getItem(position), ics214ResourcesListViewModel)
    }
    class ICSResourcesListItemViewHolder(private val binding: Ics214ListItemResourceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(icsResource: ICSResource, ics214ResourcesListViewModel : ICS214ResourcesListViewModel) {
            binding.run {
                this.resource = icsResource
                this.viewModel = ics214ResourcesListViewModel
                executePendingBindings()
            }
        }
    }
}

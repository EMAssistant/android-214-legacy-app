package com.randomvoids.emassistant.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.randomvoids.emassistant.databinding.IncidentListItemBinding
import com.randomvoids.emassistant.model.Incident
import com.randomvoids.emassistant.model.IncidentDiff

class IncidentListItemAdapter (
) : ListAdapter<Incident, IncidentListItemAdapter.IncidentListItemViewHolder>(IncidentDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentListItemViewHolder {
        return IncidentListItemViewHolder(
            IncidentListItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IncidentListItemViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class IncidentListItemViewHolder(private val binding: IncidentListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(incident: Incident) {
            binding.run {
                this.incident = incident
                executePendingBindings()
            }
        }
    }

}
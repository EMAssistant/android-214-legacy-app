package com.randomvoids.emassistant.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.randomvoids.emassistant.databinding.MyIcs214ListItemBinding
import com.randomvoids.emassistant.model.ICS214Diff
import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.view.ui.myforms.ICS214ListViewModel

class ICS214ListItemAdapter (
    private val ics214ListItemViewModel: ICS214ListViewModel
) : ListAdapter<ICS214WithActivityLogAndResources, ICS214ListItemAdapter.ICS214ListItemViewHolder>(ICS214Diff) {
    private val ics214ListItems: MutableList<ICS214WithActivityLogAndResources> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ICS214ListItemViewHolder {
        return ICS214ListItemViewHolder(
            MyIcs214ListItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun addICS214List(ics214List: List<ICS214WithActivityLogAndResources>) {
        ics214ListItems.removeAll { true }
        ics214ListItems.addAll(ics214List)
        notifyDataSetChanged()
        /* in case it's additive
        val previous = items.size
        items.addAll(pokemonList)
        notifyItemRangeChanged(previous, pokemonList.size)
         */
    }

    //override fun getItemCount(): Int = ics214ListItems.size

    override fun onBindViewHolder(holder: ICS214ListItemViewHolder, position: Int) {
        holder.bind(getItem(position), ics214ListItemViewModel)
    }

    class ICS214ListItemViewHolder(private val binding: MyIcs214ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ics214: ICS214WithActivityLogAndResources, ics214ListItemViewModel: ICS214ListViewModel) {
            binding.run {
                this.ics214 = ics214
                this.viewModel = ics214ListItemViewModel
                executePendingBindings()
            }
        }
    }

}
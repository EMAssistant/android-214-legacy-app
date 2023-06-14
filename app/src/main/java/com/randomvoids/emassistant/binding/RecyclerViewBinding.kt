package com.randomvoids.emassistant.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.model.MileageLogEntry
import com.randomvoids.emassistant.view.adapter.ICS214ListItemAdapter
import com.randomvoids.emassistant.view.adapter.MileageLogListItemAdapter
import com.skydoves.whatif.whatIfNotNullOrEmpty

@BindingAdapter("adapter")
fun bindAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter
}

@BindingAdapter("adapterICS214List")
fun bindAdapterICS214List(view: RecyclerView, ics214List: List<ICS214WithActivityLogAndResources>?) {
    ics214List.whatIfNotNullOrEmpty {
        (view.adapter as? ICS214ListItemAdapter)?.addICS214List(it)
    }
}

@BindingAdapter("adapterMileageLogList")
fun bindAdapterMileageLogList(view: RecyclerView, mileageLog: List<MileageLogEntry>?) {
    mileageLog.whatIfNotNullOrEmpty {
        (view.adapter as? MileageLogListItemAdapter)?.addList(it)
    }
}
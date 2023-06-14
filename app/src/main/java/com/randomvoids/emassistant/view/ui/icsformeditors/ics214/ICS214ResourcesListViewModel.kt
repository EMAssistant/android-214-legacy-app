package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.annotation.MainThread
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.ICS214Id
import com.randomvoids.emassistant.model.ICSResource
import com.randomvoids.emassistant.repository.ICS214Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ICS214ResourcesListViewModel @Inject constructor(
    private val ics214Repository: ICS214Repository
) : LiveCoroutinesViewModel() {
    //for the list
    private var resourcesListFetchingLiveData: MutableLiveData<Int> = MutableLiveData()
    val resourcesListLiveData : LiveData<List<ICSResource>?>
    //for editing an individual entry
    private var icsResourceFetchingIdLiveData: MutableLiveData<Int> = MutableLiveData()
    val icsResourceLiveData: MutableLiveData<ICSResource?>
    val isAtMaxResources: ObservableBoolean = ObservableBoolean(false)

    init {
        Timber.d("Initializing ICS214ResourcesListViewModel")
        resourcesListLiveData = resourcesListFetchingLiveData.switchMap {
            launchOnViewModelScope {
                ics214Repository.getResourcesBy214Id(it, onSuccess = {
                        if(it < 8)
                            isAtMaxResources.set(false)
                        else
                            isAtMaxResources.set(true)
            }).asLiveData()
            }
        }
        icsResourceLiveData = icsResourceFetchingIdLiveData.switchMap {
            launchOnViewModelScope {
                if(icsResourceFetchingIdLiveData.value!! < 1)
                    MutableLiveData(ICSResource(ics214Id = resourcesListFetchingLiveData.value!!, name = "", icsPosition = "", homeAgency = ""))
                else
                    ics214Repository.getResourceItemById(it).asLiveData()
            }
        } as MutableLiveData<ICSResource?>
    }

    @MainThread
    fun onICSResourceItemEditButtonClick(view: View, icsResourceItemId: Int) {
        val icsResourceItemDialogFragment = ICS214ResourceDialogFragment()
        icsResourceItemDialogFragment.setTargetFragment(FragmentManager.findFragment(view), 9000)
        icsResourceItemDialogFragment.show((FragmentManager.findFragment(view) as Fragment).parentFragmentManager, "ics_resource_item_dialog")
        icsResourceFetchingIdLiveData.value=icsResourceItemId
    }

    @MainThread
    fun onICSResourceItemDeleteButtonClick(view: View, icsResourceItemId: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this resource item?")
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener() { _: DialogInterface, _: Int ->
                GlobalScope.launch {
                    ics214Repository.deleteResourceItemById(icsResourceItemId)
                    resourcesListFetchingLiveData.postValue(resourcesListFetchingLiveData.value)
                }
            })
            .setNegativeButton(android.R.string.cancel, null).show()
        icsResourceFetchingIdLiveData.value=icsResourceItemId //refresh the input page... except shouldn't do this. only want to refresh the list...
        //TODO fix this!
    }

    fun onResourceItemNewButtonClick(view:View) {
        val icsResourceItemDialogFragment = ICS214ResourceDialogFragment()
        icsResourceItemDialogFragment.setTargetFragment(FragmentManager.findFragment(view), 9000)
        icsResourceItemDialogFragment.show((FragmentManager.findFragment(view) as Fragment).parentFragmentManager, "ics_resource_item_dialog")
        icsResourceFetchingIdLiveData.value=-1

    }

    fun saveActiveResourceItemEdit() {
        GlobalScope.launch {
            icsResourceLiveData.value?.let { ics214Repository.saveICSResource(it) }
            resourcesListFetchingLiveData.postValue(resourcesListFetchingLiveData.value)
            icsResourceFetchingIdLiveData.postValue(icsResourceFetchingIdLiveData.value)
            Timber.d("save called")
        }
    }

    @MainThread
    fun fetchICSResourceList(id: ICS214Id) {
        resourcesListFetchingLiveData.value = id
    }

}
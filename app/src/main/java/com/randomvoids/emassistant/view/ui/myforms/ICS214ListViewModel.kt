package com.randomvoids.emassistant.view.ui.myforms

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.navigation.findNavController
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.ICS214Id
import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.repository.ICS214Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ICS214ListViewModel @Inject constructor(
    private val ics214Repository: ICS214Repository
) : LiveCoroutinesViewModel() {
    private var listFetchingUpdater: LiveData<Int> = ics214Repository.getLiveICS214Count().asLiveData()
    val ics214ListLiveData: LiveData<List<ICS214WithActivityLogAndResources>?>

    init {
        Timber.d("initializing list")
        ics214ListLiveData = listFetchingUpdater.switchMap {
            launchOnViewModelScope {
                this.ics214Repository.getAllFullICS214s().asLiveData()
            }
        }
    }

    fun onActivityLogEntryEditButtonClick(view: View, id: ICS214Id, incidentName: String) {
        val action = ICS214ListFragmentDirections.actionNavigateToIcs214Editor(id, incidentName)
        view.findNavController().navigate(action)
    }



    fun seed() {
        GlobalScope.launch {
            ics214Repository.seed()
        }
    }
}
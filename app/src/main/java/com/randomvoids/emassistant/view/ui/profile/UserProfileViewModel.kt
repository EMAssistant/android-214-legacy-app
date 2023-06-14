package com.randomvoids.emassistant.view.ui.profile

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.User
import com.randomvoids.emassistant.repository.ICS214Repository
import com.randomvoids.emassistant.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val icS214Repository: ICS214Repository
) : LiveCoroutinesViewModel() {
    private val fetchedUserLiveData :  MutableLiveData<User> = MutableLiveData()
    val userLiveData : MutableLiveData<User>

    init {
        Timber.d("initializing UserProfileViewModel")


        userLiveData = fetchedUserLiveData.switchMap {
            launchOnViewModelScope {
                Timber.d("in launchonviewmodelscope")
                this.userProfileRepository.fetch(1).asLiveData()
            }
        } as MutableLiveData<User>
    }


    fun onClickSave() {
        Timber.d("onClickSave called ")
        if(fetchedUserLiveData.value != userLiveData.value) {
            GlobalScope.launch (Dispatchers.Main) { // Dispatchers.Main because only the Main thread can touch UI elements. Otherwise you may wish to use Dispatchers.IO instead!
                userProfileRepository.update(userLiveData.value!!)
                Timber.d("updated")
                fetchedUserLiveData.postValue(userLiveData.value)
            }
        }
    }

    @MainThread
    fun seed() {
        GlobalScope.launch {
            userProfileRepository.seed()
        }
    }

    fun clearOut() {
        GlobalScope.launch {
            var firstOne = true
            val all214s = icS214Repository.getAllFullICS214s().asLiveData().value!!
            for(ics214 in all214s)
                if(firstOne)
                    firstOne=false
                else
                    icS214Repository.deleteICS214(ics214)
        }
    }
    @MainThread
    fun fetchUserProfile() {
        Timber.d("fetching profile")
        fetchedUserLiveData.postValue((launchOnViewModelScope { this.userProfileRepository.fetch(1).asLiveData() }).value)
    }
}
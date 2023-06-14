package com.randomvoids.emassistant.base

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers

abstract class LiveCoroutinesViewModel : ViewModel(), LifecycleObserver {

    inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }
}
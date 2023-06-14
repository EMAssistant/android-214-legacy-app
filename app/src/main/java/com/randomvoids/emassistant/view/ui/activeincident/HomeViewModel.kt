package com.randomvoids.emassistant.view.ui.activeincident

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "TODO: implement this"
    }
    val text: LiveData<String> = _text
}
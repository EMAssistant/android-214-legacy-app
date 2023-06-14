package com.randomvoids.emassistant.extensions

import androidx.databinding.Observable

fun <T: Observable> T.addOnPropertyChanged(callback: (T) -> Unit) =
    object: Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(observable: Observable?, i: Int) =
            callback(observable as T)
    }.also { addOnPropertyChangedCallback(it) }
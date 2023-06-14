package com.randomvoids.emassistant.binding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.randomvoids.emassistant.extensions.gone


@BindingAdapter("gone")
fun bindGone(view: View, shouldBeGone: Boolean) {
    view.gone(shouldBeGone)
}

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}
//https://stackoverflow.com/questions/41154446/android-databinding-float-to-textview
@BindingAdapter("android:text")
fun setFloat(view: TextView, value: Float) {
    if (value.isNaN()) view.text = ""
    else view.text = "%.1f".format(value)
}

@InverseBindingAdapter(attribute = "android:text")
fun getFloat(view: TextView): Float {
    val num = view.text.toString()
    if(num.isNullOrEmpty()) return 0.0F
    return try {
        num.toFloat()
    } catch (e: NumberFormatException) {
        0.0F
    }
}

@BindingAdapter("android:text")
fun setInt(view: TextView, value: Int) {
    view.text = ""+value
}

@InverseBindingAdapter(attribute = "android:text")
fun getInt(view: TextView): Int {
    val num = view.text.toString()
    if(num.isNullOrEmpty()) return 0
    return try {
        num.toInt()
    } catch (e: NumberFormatException) {
        0
    }
}
package com.randomvoids.emassistant.view.ui.dialogs

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerDialogFragment(
    val entryTime: Date?
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        entryTime?.let { c.time = it }
        return TimePickerDialog(requireContext(), this, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        entryTime?.let { c.time = it }
        val date = Date.from(GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0).toInstant())
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent().putExtra("selectedTime", date))
    }
}
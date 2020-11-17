package com.foodsgully.foodsgullyuser.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.foodsgully.foodsgullyuser.utils.listeners.DateChangeListener
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import java.util.*

class FGDatePickerDialog(private val dialogContext: Context, private val dateChangeListener : DateChangeListener, private val selectedDate: Date?) : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        if(selectedDate != null) calendar.time = selectedDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog =  DatePickerDialog(dialogContext, this, year, month, day)
        datePickerDialog.datePicker.minDate = Date().time
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year,month,dayOfMonth)
        dateChangeListener.onDateChanged(FoodsGullyUtils.getDateString(calendar.time,FoodsGullyConstants.POST_DATE_FORMAT))
    }

}
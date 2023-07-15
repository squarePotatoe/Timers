package com.mjdoescode.simpletimerapp.utils

import android.widget.NumberPicker

class NumberPickerHelper(private val numberPicker: NumberPicker, private val viewToUpdate: ViewToUpdate): NumberPicker.OnValueChangeListener {
    interface ViewToUpdate {
        fun updateValue(newValue: Int)
    }

    init {
        numberPicker.setOnValueChangedListener(this)
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if (newVal > 0){
            viewToUpdate.updateValue(newVal)
        } else {
            viewToUpdate.updateValue(0)
        }
    }

}
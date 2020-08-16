/*
 * Copyright (C) 2017 Álinson Santos Xavier
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see .
 */

package org.isoron.uhabits.activities.common.dialogs

import android.content.*
import android.graphics.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.*
import org.isoron.androidbase.activities.*
import org.isoron.androidbase.utils.*
import org.isoron.uhabits.*
import org.isoron.uhabits.core.models.*
import org.isoron.uhabits.core.ui.screens.habits.list.*
import javax.inject.*


class CheckmarkOptionPickerFactory
@Inject constructor(
        @ActivityContext private val context: Context
) {
    fun create(habitName: String,
               habitTimestamp: String,
               value: Int,
               callback: ListHabitsBehavior.CheckmarkOptionsCallback): AlertDialog {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.checkmark_option_picker_dialog, null)
        val title = context.resources.getString(
                R.string.choose_checkmark_option, habitName, habitTimestamp)
        val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setTitle(title)
                .setOnDismissListener{
                    callback.onCheckmarkOptionDismissed()
                }
                .create()

        val buttonValues = mapOf(
                R.id.check_button to Checkmark.CHECKED_EXPLICITLY,
                R.id.skip_button to Checkmark.SKIPPED_EXPLICITLY,
                R.id.fail_button to Checkmark.FAILED_EXPLICITLY_NECESSARY,
                R.id.clear_button to Checkmark.UNCHECKED
        )
        val valuesToButton = mapOf(
                Checkmark.CHECKED_EXPLICITLY to R.id.check_button,
                Checkmark.SKIPPED_EXPLICITLY to R.id.skip_button ,
                Checkmark.FAILED_EXPLICITLY_NECESSARY to R.id.fail_button,
                Checkmark.FAILED_EXPLICITLY_UNNECESSARY to R.id.fail_button
        )

        for ((buttonId, buttonValue) in buttonValues) {
            val button = view.findViewById<Button>(buttonId)
            var textStyle = Typeface.NORMAL
            button.setOnClickListener{
                callback.onCheckmarkOptionPicked(buttonValue)
                dialog.dismiss()
            }
            if (valuesToButton.containsKey(value) &&  valuesToButton[value] == buttonId)
                textStyle = Typeface.BOLD

            button.setTypeface(InterfaceUtils.getFontAwesome(context), textStyle)
        }

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        return dialog
    }
}

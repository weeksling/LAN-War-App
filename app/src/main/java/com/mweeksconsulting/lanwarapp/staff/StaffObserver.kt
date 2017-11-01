package com.mweeksconsulting.lanwarapp.staff

import android.content.Context
import android.os.Bundle

/**
 * Used for the observable pattern to update the staff staff list
 */
interface StaffObserver {

        fun refreshStaff (staffArray: ArrayList<Staff>)
        val context: Context

}
package com.mweeksconsulting.lanwarapp.Staff_Package

import android.content.Context
import android.os.Bundle
import com.mweeksconsulting.lanwarapp.Sponsor_Package.Sponsor

/**
 * Created by michael on 21/10/17.
 */
interface StaffObserver {

        fun setDefaultRI(bundle: Bundle)
        fun cloudSponsors(sponsorArray: ArrayList<Staff>)

        val context: Context

}
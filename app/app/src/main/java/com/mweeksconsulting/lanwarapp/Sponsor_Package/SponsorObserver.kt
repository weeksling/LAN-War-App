package com.mweeksconsulting.lanwarapp.Sponsor_Package

import android.content.Context
import android.os.Bundle

/**
 * Created by michael on 16/10/17.
 */
interface SponsorObserver {
    fun setDefaultRI(bundle: Bundle)
    fun cloudSponsors(sponsorArray: ArrayList<Sponsor>)
    val context:Context
}
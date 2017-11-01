package com.mweeksconsulting.lanwarapp.sponsor

import android.content.Context
import android.os.Bundle

/**
 * Created by michael on 16/10/17.
 * Observer
 */
interface SponsorObserver {
    val context:Context
    //fun setDefaultRI(bundle: Bundle)
    fun refreshSponsors(sponsorArray: ArrayList<Sponsor>)
}
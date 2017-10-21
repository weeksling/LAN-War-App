package com.mweeksconsulting.lanwarapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

/**
 * Created by michael on 16/10/17.
 */
interface SponsorObserver {
    fun setDefaultRI(bundle: Bundle)
    fun cloudSponsors(sponsorArray: ArrayList<Sponsor>)
    val context:Context
}
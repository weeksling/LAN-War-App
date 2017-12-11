package com.mweeksconsulting.lanwarapp.sponsor

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 17/10/17.
 * The sponsor object, image is replaced with default lanwar Icon if staff does not want their face online
 */

@Entity
class Sponsor(@PrimaryKey val name: String, val description: String, val imagePath: String, val createDate:String,val webSite: String?) {
    fun getImageView(imageView: ImageView):ImageView{
        Log.i("Sponsor Adapter", "Setting sponsor image ")
        Log.i("Sponsor Adapter", imagePath)

        val file = File(imagePath)
        when (file.exists() && file.isFile) {
            true -> {
                println("file found: " + imagePath)
                Picasso.with(context).load(file).into(imageView)
            }
            false -> {
                println("file not found use lanwar icon")
                Picasso.with(context).load(R.drawable.lanwar).into(imageView)
            }
        }
        return imageView
    }

    override fun toString(): String {
        return ("name: " + name +" description: " + description + " imagePath: " +imagePath + " createDate: " + createDate + " website: " +webSite)
    }
}
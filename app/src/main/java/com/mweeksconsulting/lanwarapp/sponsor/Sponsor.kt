package com.mweeksconsulting.lanwarapp.sponsor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mweeksconsulting.lanwarapp.R
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 17/10/17.
 * The sponsor object, image is replaced with default lanwar Icon if staff does not want their face online
 */
data class Sponsor(val name: String, val description: String, val imagePath: String, val context: Context) :Serializable {
    val bitmap:Bitmap
    init{
        println("Setting sponsor")
        val file = File(imagePath)
        bitmap = when(file.exists()) {
         true ->   {println("file found: " + imagePath)
             BitmapFactory.decodeStream(file.inputStream())}
         false->  {println("file not found use lanwar icon")

             BitmapFactory.decodeResource(context.resources, R.mipmap.lanwar_icon)}
        }
    }
}
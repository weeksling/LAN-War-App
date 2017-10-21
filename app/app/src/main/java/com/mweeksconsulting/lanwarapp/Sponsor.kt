package com.mweeksconsulting.lanwarapp

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 17/10/17.
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
             BitmapFactory.decodeResource(context.resources,R.mipmap.lanwar_icon)}
        }
    }
}
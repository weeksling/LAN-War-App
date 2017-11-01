package com.mweeksconsulting.lanwarapp.staff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mweeksconsulting.lanwarapp.R
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 21/10/17.
 * The Staff object
 */
data class Staff(val name:String,val alias:String,val role:String,val imgPath:String,val context: Context):Serializable {
    val bitmap: Bitmap
    init{
        println("Setting staff")
        val file = File(imgPath)
        bitmap = when(file.exists() && file.isFile) {
            true ->   {println("file found: " + imgPath)
                BitmapFactory.decodeStream(file.inputStream())}
            false->  {println("file not found use lanwar icon")
                BitmapFactory.decodeResource(context.resources, R.mipmap.lanwar_icon)}
        }
    }

}
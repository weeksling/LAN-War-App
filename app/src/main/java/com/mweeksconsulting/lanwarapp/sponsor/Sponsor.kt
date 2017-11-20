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
import com.mweeksconsulting.lanwarapp.R
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 17/10/17.
 * The sponsor object, image is replaced with default lanwar Icon if staff does not want their face online
 */

@Entity
class Sponsor(@PrimaryKey val name: String, val description: String, val imagePath: String, val createDate:String) {
    @Ignore
    lateinit var context:Context

    fun getBitMap() :Bitmap{
        Log.i("Sponsor Adapter", "Setting sponsor image ")
        Log.i("Sponsor Adapter", imagePath)

        val file = File(imagePath)
       val bitmap = when (file.exists()) {
            true -> {
                println("file found: " + imagePath)
                BitmapFactory.decodeStream(file.inputStream())
            }
            false -> {
                println("file not found use lanwar icon")
                BitmapFactory.decodeResource(context.resources,R.mipmap.lanwar_icon)
            }
        }

        if(bitmap == null){
            return                 BitmapFactory.decodeResource(context.resources,R.mipmap.lanwar_icon)
        }else {
            return bitmap
        }
    }

    override fun toString(): String {
        return ("name: " + name +" description: " + description + " imagePath: " +imagePath + " createDate: " + createDate)
    }
}
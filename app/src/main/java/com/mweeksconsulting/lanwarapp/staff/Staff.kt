package com.mweeksconsulting.lanwarapp.staff

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.R
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 21/10/17.
 * The Staff object
 */
@Entity
data class Staff(val name:String,val alias:String,val role:String,val imgPath:String, val createDate:String):Serializable {
    @PrimaryKey(autoGenerate = true)
    var id : Int =0

    @Ignore
    val bitmap: Bitmap
    init{
        println("Setting staff")
        val file = File(imgPath)
        bitmap = when(file.exists() && file.isFile) {
            true ->   {println("file found: " + imgPath)
                BitmapFactory.decodeStream(file.inputStream())}
            false->  {println("file not found use lanwar icon")
                val context = LanWarApplication.appSingleton.context
                BitmapFactory.decodeResource(context.resources, R.mipmap.lanwar_icon)}
        }
    }

}
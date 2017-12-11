package com.mweeksconsulting.lanwarapp.staff

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.Serializable

/**
 * Created by michael on 21/10/17.
 * The Staff object
 */
@Entity
data class Staff(val name:String,val alias:String,val role:String,val imgPath:String, val createDate:String) {
    @PrimaryKey(autoGenerate = true)
    var id : Int =0


    fun  getImageView (imageView: ImageView):ImageView{
        println("Setting staff")
        val file = File(imgPath)
        when (file.exists() &&file.isFile) {
            true -> {
                Picasso.with(context).load(file).into(imageView)
            }
            false -> {
                println("file not found use lanwar icon")
                //use drawable
                Picasso.with(context).load(R.drawable.lanwar).into(imageView)
            }
        }
        return imageView
    }




}
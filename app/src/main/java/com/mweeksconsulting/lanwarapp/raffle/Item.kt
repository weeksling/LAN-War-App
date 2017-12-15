package com.mweeksconsulting.lanwarapp.raffle

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.Serializable
import java.util.*

/**
 * Created by michael on 13/12/17.
 * The item class that holds all the item data
 * relies on the raffle id
 * gets the bitmap image when asked
 * uses Piccasso to load the image
 */
@Entity
(foreignKeys =  arrayOf(ForeignKey
(entity = Raffle::class,
        parentColumns = ["id"],
        childColumns = ["raffleID"],
        onDelete = ForeignKey.CASCADE)),
        indices= arrayOf(Index (value="raffleID")))

class Item( val raffleID :Long, val itemTitle:String, val imagePath: String?):Serializable{
    @PrimaryKey (autoGenerate = true)
    var id =0

    fun getImageView (imageView: ImageView): ImageView {
        println("Setting staff")
        when (imagePath !=null && (File(imagePath).exists() &&File(imagePath).isFile)) {
            true -> {
                Picasso.with(LanWarApplication.appSingleton.context).load(File(imagePath)).into(imageView)
            }
            false -> {
                println("file not found use lanwar icon")
                //use drawable
                Picasso.with(LanWarApplication.appSingleton.context).load(R.drawable.lanwar).into(imageView)
            }
        }
        return imageView
    }

    override fun toString(): String {
        return "id: $id, raffle id: $raffleID, title: $itemTitle, imagePath: $imagePath"
    }


}
package com.mweeksconsulting.lanwarapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.graphics.drawable.Drawable
import java.io.File


/**
 * Created by michael on 17/10/17.
 */
class SponsorAdapter  (context: Context, res : Int, var data: ArrayList<Sponsor> ):
        ArrayAdapter<Sponsor>(context,res,data){

    override fun getView(position: Int, convertView: View? , parent: ViewGroup?): View? {
        val holder = ViewHolder()
        var returnView : View
        if(convertView==null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val inflate= inflater.inflate(R.layout.sponsor_linear_layout, null)
            holder.descriptionText = inflate.findViewById<TextView>(R.id.description)
            holder.SponsorImageView = inflate.findViewById<ImageView >(R.id.SponsorImage)
            inflate.tag = holder
            returnView = inflate
        }  else {
            holder.SponsorImageView = convertView.findViewById<ImageView>(R.id.SponsorImage)
            holder.descriptionText = convertView.findViewById<TextView>(R.id.description)

            convertView.tag = holder
            returnView = convertView
        }

        val sponsor:Sponsor = data[position]

            holder.SponsorImageView.setImageBitmap(sponsor.bitmap)
            holder.descriptionText.text = sponsor.description

        return returnView
    }


    internal class ViewHolder {
        lateinit var descriptionText: TextView
        lateinit  var SponsorImageView: ImageView

    }

}

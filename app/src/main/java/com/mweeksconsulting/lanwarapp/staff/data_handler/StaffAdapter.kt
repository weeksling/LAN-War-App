package com.mweeksconsulting.lanwarapp.sponsor.data_handler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor


/**
 * Created by michael on 17/10/17.
 * The Sponsor adapter makes the sponsor list look nice and
 * uses the view holder pattern for smooth scrolling
 */
class StaffAdapter(context: Context, res : Int, var data: List<Sponsor> ):
        ArrayAdapter<Sponsor>(context,res,data){

    override fun getView(position: Int, convertView: View? , parent: ViewGroup?): View? {
        val holder = ViewHolder()
        val returnView : View
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

        val sponsor: Sponsor = data[position]
        sponsor.context=context
            holder.SponsorImageView.setImageBitmap(sponsor.getBitMap())
            holder.descriptionText.text = sponsor.description

        return returnView
    }


    internal class ViewHolder {
        lateinit var descriptionText: TextView
        lateinit  var SponsorImageView: ImageView

    }

}

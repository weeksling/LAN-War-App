package com.mweeksconsulting.lanwarapp.Staff_Package

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Sponsor_Package.Sponsor

/**
 * Created by michael on 17/10/17.
 */
class StaffAdapter(context: Context, res : Int, var data: ArrayList<Staff> ):
        ArrayAdapter<Staff>(context,res,data){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder = ViewHolder()
        var returnView : View
        if(convertView==null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val inflate= inflater.inflate(R.layout.staff_list_layout, null)
            holder.staffName = inflate.findViewById(R.id.Staff_Name)
            holder.roleName = inflate.findViewById(R.id.StaffRole)
            holder.alias = inflate.findViewById(R.id.staff_alias)
            holder.staffImage = inflate.findViewById(R.id.StaffImage)

            inflate.tag = holder
            returnView = inflate
        }  else {
            holder.staffName = convertView.findViewById(R.id.Staff_Name)
            holder.roleName = convertView.findViewById(R.id.StaffRole)
            holder.alias = convertView.findViewById(R.id.staff_alias)
            holder.staffImage = convertView.findViewById(R.id.StaffImage)
            convertView.tag = holder
            returnView = convertView
        }

        val staff: Staff = data[position]
        holder.staffName.setText(staff.name)
        holder.roleName.setText(staff.role)
        holder.alias.setText(staff.alias)
        holder.staffImage.setImageBitmap(staff.bitmap)


        return returnView
    }


    internal class ViewHolder {
        lateinit var staffName: TextView
        lateinit var alias: TextView
        lateinit var roleName: TextView
        lateinit  var staffImage: ImageView

    }

}
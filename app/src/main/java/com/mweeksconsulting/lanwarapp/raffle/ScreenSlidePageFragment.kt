package com.mweeksconsulting.lanwarapp.raffle

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillValue
import android.widget.ImageView
import android.widget.TextView
import com.mweeksconsulting.lanwarapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*

/**
 * Created by michael on 12/12/17.
 * This is what is shown as the raffle item name and image
 */
class ScreenSlidePageFragment:Fragment() {
    private var itemList:Array<Item>? = null
    private var pos:Int=0

    object Instance{
        fun getFragment(items:Array<Item>?, newPos:Int): Fragment {
            val fragment = ScreenSlidePageFragment()
            val bundle = Bundle()
            bundle.putSerializable("ITEM",items)
            bundle.putSerializable("POS",newPos)
            fragment.arguments = bundle

            Log.i("Screen Slide","fragment instance")

            return fragment
        }
    }

    //load the image
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemList = arguments?.getSerializable("ITEM") as Array<Item>?
        val tmp= arguments?.getInt("POS")
        if(tmp!=null) {
            pos = tmp
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.image_gallery_fragment, container, false) as ViewGroup
        Log.i("Screen Slide","On Create View")
        val imageView = root.findViewById<ImageView>(R.id.item_image)
        val textView = root.findViewById<TextView>(R.id.Title)

        Log.i("Screen slide", "pos: $pos")

            val item = itemList?.get(pos)
            if (item == null) {
                Log.i("Screen slide", "item is null")

                Picasso.with(context).load(R.drawable.lanwar).into(imageView)
            } else {
                Log.i("Screen slide", "item is not null")
                Log.i("Screen slide", "item $item")

                item.getImageView(imageView)
            }

        textView.text=item?.itemTitle
        Log.i("Screen slide", "item name text: ${textView.text}")

        return  root
    }
}
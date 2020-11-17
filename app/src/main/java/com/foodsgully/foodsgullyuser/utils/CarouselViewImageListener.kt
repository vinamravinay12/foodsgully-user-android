package com.foodsgully.foodsgullyuser.utils

import android.widget.ImageView
import com.synnapps.carouselview.ImageListener

class CarouselViewImageListener(private val images : Array<Int>) : ImageListener {
    override fun setImageForPosition(position: Int, imageView: ImageView?) {
        if(images.isEmpty()) return
        imageView?.setImageResource(images[position])
    }
}
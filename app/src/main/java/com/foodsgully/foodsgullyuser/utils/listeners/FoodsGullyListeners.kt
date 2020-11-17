package com.foodsgully.foodsgullyuser.utils.listeners

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.foodsgully.foodsgullyuser.models.responsemodels.Product


interface ItemClickListener {
    fun onItemClick(item : Any?)
}


interface ViewInteractionHandler {
    fun setContext(context: Context)
}


interface KeyItemActionListener {
    fun onKeyEvent(valueEntered: String, data: Any?)
}

interface CheckChangeListener {
    fun onCheckChanged(item : Any?)
}


interface DateChangeListener {
    fun onDateChanged(date : String?)
}

interface CallUserListener {
    fun callUser(number : String)
}

interface UpdateProductInCartListener {
    fun updateProduct(product : Product,quantity : Int)
}

interface ImageBitmapDownloadedListener {
    fun onBitmapReady(bitmap : Bitmap,uri: Uri)
}


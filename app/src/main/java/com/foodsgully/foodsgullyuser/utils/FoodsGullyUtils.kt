package com.foodsgully.foodsgullyuser.utils


import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.foodsgully.foodsgullyuser.BuildConfig
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.firebase.FGFCMNotificationService
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import dmax.dialog.SpotsDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object FoodsGullyUtils {

    @JvmStatic
    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


    @JvmStatic
    fun showSnackbar(message: String, root: View) {

        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showLoaderProgress(message: String, context: Context): android.app.AlertDialog? {
        if(!isContextActive(context)) return null
        return SpotsDialog.Builder()
            .setContext(context)
            .setTheme(R.style.progress_dialog)
            .setMessage(message)
            .build()
            .apply {
                show()
            }
    }

    fun isContextActive(context: Context) : Boolean{
        if ( context is Activity ) {
            return !context.isFinishing
        }
        return true
    }

    @JvmStatic
    fun dismissProgress(progressDialog: AlertDialog?) {
        if(progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()
    }

    @JvmStatic
    fun getToken(context: Context): String? {
        return "Bearer " + SharedPreferenceManager(
            context,
            FoodsGullyConstants.LOGIN_SP
        ).getStringPreference(FoodsGullyConstants.USER_TOKEN)?.removePrefix("\"")?.removeSuffix("\"")
    }


    @JvmStatic
    fun getDefaultErrorMessage(context: Context?): String {

        return context?.getString(R.string.something_went_wrong) ?: FoodsGullyConstants.SWW
    }

    @JvmStatic
    fun initializeAndSetCarouselImages(carouselView: CarouselView,images : Array<Int>, imageListener : ImageListener) {
        try {
            carouselView.setImageListener(imageListener)
            carouselView.pageCount = images.count()

        } catch (exception : Exception) {
            exception.printStackTrace()
        }
    }


    fun setCategoryImage(imageView : ImageView,uri : Uri,context: Context,placeholderImage : Int,category: Category?) {

        if(!isContextActive(context)) return
        Glide.with(context).asBitmap().load(uri).placeholder(placeholderImage)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .thumbnail(0.3f)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageView.setImageBitmap(resource)
                    category?.categoryImageBitmap = resource

                }

            })
    }

    fun setUserImageToView(imageView : ImageView,uri : Uri,context: Context,placeholderImage : Int,thumbnailMultiplier : Float = 0.3f, user : User?)  {

        if(!isContextActive(context)) return
        Glide.with(context).asBitmap().load(uri).placeholder(placeholderImage)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .thumbnail(thumbnailMultiplier)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageView.setImageBitmap(resource)
                    user?.image = resource
                }

            })

    }

    fun setImageToView(imageView : ImageView,uri : Uri,context: Context,placeholderImage : Int,thumbnailMultiplier : Float = 0.3f)  {

        if(!isContextActive(context)) return
        Glide.with(context).asBitmap().load(uri).placeholder(placeholderImage)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .thumbnail(thumbnailMultiplier)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageView.setImageBitmap(resource)
                }

            })

    }

    fun convertDateStringIntoMillis(date: String?, time: String?, dateFormat: String, timeFormat: String): Long {
        val combinedDate = "$date $time"

        val convertedDate = getDate(combinedDate, dateFormat)

        return (convertedDate?.time ?: 0)
    }


    fun getDateString(date: Date?, postDateFormat: String): String? {
        val simpleDateFormat = SimpleDateFormat(postDateFormat, Locale.ENGLISH)
        date?.let { return simpleDateFormat.format(it) }
        return null
    }

    fun convertDate(dateString: String?, oldFormat: String, newFormat: String): String? {
        return try {
            val oldDateFormat = SimpleDateFormat(oldFormat, Locale.ENGLISH)
            val newDateFormat = SimpleDateFormat(newFormat, Locale.ENGLISH)
            val date = oldDateFormat.parse(dateString)
            date?.let { newDateFormat.format(date) }
        } catch (exception: Exception) {
            ""
        }
    }

    fun getDate(dateString: String?, format: String): Date? {

        return SimpleDateFormat(format, Locale.ENGLISH).parse(dateString)
    }

    fun getDateString(timeInMillis: Long, dateFormat: String): String? {
        val differenceInHours = timeInMillis / (1000 * 60 * 60)
        val differenceInMinutes = timeInMillis / (1000 * 60) % 60

        return java.lang.StringBuilder().append(if (differenceInHours < 10) "0" else "").append(differenceInHours).append(":").append(if (differenceInMinutes < 10) "0" else "").append(differenceInMinutes).toString().trim()
    }

    fun getItemsInCart(context: Context): ArrayList<CartItem> {
        val cartItemJson = SharedPreferenceManager(context,FoodsGullyConstants.CATEGORIES_SP).getStringPreference(FoodsGullyConstants.KEY_CART_ITEMS)

        return if(cartItemJson.isNullOrEmpty()) ArrayList()
        else Gson().fromJson(cartItemJson,object : TypeToken<List<CartItem>>() {}.type)
    }

    fun saveItemInCart(context: Context, cartItems: ArrayList<CartItem>) {
        SharedPreferenceManager(context,FoodsGullyConstants.CATEGORIES_SP).storeComplexObjectPreference(FoodsGullyConstants.KEY_CART_ITEMS,cartItems)

    }

    fun getRGBFromHex(color: String?): Triple<Int,Int,Int> {

        return if(color.isNullOrEmpty())  Triple(255,255,255)
        else Triple(Color.red(Color.parseColor(color)),
            Color.green(Color.parseColor(color)),
            Color.blue(Color.parseColor(color)))

    }

    fun getDeliveryAddress(context: Context) : HomeAddress? {

        return getCurrentUser(context)?.homeAddress
    }

    fun getCurrentUser(context: Context) : User? {
        val userJson = SharedPreferenceManager(context,FoodsGullyConstants.LOGIN_SP).getStringPreference(FoodsGullyConstants.USER_DATA)
       return Gson().fromJson<User>(userJson,User::class.java)
    }

    fun updateHomeAddress(homeAddress: HomeAddress?,context: Context) {
        val user = getCurrentUser(context)
        user?.homeAddress = homeAddress
        storeUpdatedUser(user,context)

    }

     fun storeUpdatedUser(user: User?, context: Context) {
         SharedPreferenceManager(context,FoodsGullyConstants.LOGIN_SP).storeComplexObjectPreference(FoodsGullyConstants.USER_DATA,user)
    }

    fun clearCartItems(context: Context) {

        SharedPreferenceManager(context,FoodsGullyConstants.CATEGORIES_SP).removeKey(FoodsGullyConstants.KEY_CART_ITEMS)
    }


    fun callForDelivery(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:9871810563")
        context.startActivity(intent)
    }

    fun hideKeyboard(view: View,context: Context?) : Boolean {
        val inputMethodManager :  InputMethodManager? = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        return true
    }

    fun getPlacesClient(context: Context) : PlacesClient {
        Places.initialize(context, BuildConfig.GOOGLE_API_KEY)
        return Places.createClient(context)
    }

    fun getSearchManager(activity: Activity)  : SearchManager? {

        if(activity is MainActivity) {
            return activity.getSearchManager()
        }

        return null
    }

    fun <V : View?> findChildrenByClass(
        viewGroup: ViewGroup,
        clazz: Class<V>
    ): Collection<V>? {
        return gatherChildrenByClass(viewGroup, clazz, ArrayList())
    }

    private fun <V : View?> gatherChildrenByClass(
        viewGroup: ViewGroup,
        clazz: Class<V>,
        childrenFound: List<V>
    ): Collection<V>? {
        for (i in 0 until viewGroup.childCount) {
            val child: View = viewGroup.getChildAt(i)
            if (clazz.isAssignableFrom(child.javaClass)) {
                childrenFound.toMutableList().add(child as V)

            }
            if (child is ViewGroup) {
                gatherChildrenByClass(child as ViewGroup, clazz, childrenFound)
            }
        }
        return childrenFound
    }

    fun clearViewModel(activity: FragmentActivity) {
        activity.viewModelStore.clear()

    }

    fun checkIfDeliverableLocation(latitude: Double, longitude: Double) : Boolean {
        val destinationLocation = Location("End")
        destinationLocation.latitude = latitude
        destinationLocation.longitude = longitude

        val startLocation = Location("Start")
        startLocation.latitude = 26.788825
        startLocation.longitude = 80.974756

        return startLocation.distanceTo(destinationLocation) <= 10000f

    }

    fun getFirebaseToken(context: Context): String? = SharedPreferenceManager(context,FoodsGullyConstants.FCM_SP).getStringPreference(FoodsGullyConstants.FIREBASE_TOKEN)

    fun getNotificationCount(context: Context): Int {
        return SharedPreferenceManager(context,FoodsGullyConstants.FCM_SP).getIntegerPreference(FoodsGullyConstants.KEY_NOTIFICATION_COUNT,0)
    }

    fun setNotificationCount(notificationCount: Int, context: Context) {
        SharedPreferenceManager(context,FoodsGullyConstants.FCM_SP).storeIntegerPreference(FoodsGullyConstants.KEY_NOTIFICATION_COUNT,notificationCount)
    }

}
package com.foodsgully.foodsgullyuser.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.databinding.ActivityMainBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.fragments.AddressFragment
import com.foodsgully.foodsgullyuser.fragments.CheckoutFragment
import com.foodsgully.foodsgullyuser.fragments.ProfileFragment
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.razorpay.PaymentResultListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import ru.nikartm.support.ImageBadgeView
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), FileDownloadResponseHandler, PaymentResultListener {

    private lateinit var bindingMainActivity: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var cartBadgeView: ImageBadgeView
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar


    private lateinit var tvProfileName: TextView

    private lateinit var ivUserImage: CircleImageView

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var mSearchManager: SearchManager

    private lateinit var cartLayout: ConstraintLayout

    private var currentUser: User? = null

    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingMainActivity = DataBindingUtil.setContentView(this, R.layout.activity_main)

        checkForAppUpdates()
        setupToolbar()

        initializeViews()

        if(intent.hasExtra(FoodsGullyConstants.ARG_IS_ORDER_PLACED_NOTIFICATION)) {
           if(intent.getBooleanExtra(FoodsGullyConstants.ARG_IS_ORDER_PLACED_NOTIFICATION,false)) {
               findNavController(R.id.nav_host_fragment).navigate(R.id.nav_order_history)
           }
        }


    }


    private fun checkForAppUpdates() {

        val numberOfFailedOrCancelledAttempts = getNumberOfUpdatedFailedOrCancelled()

        val appUpdateManager = AppUpdateManagerFactory.create(this)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

            ) {
                updateApp(
                    appUpdateManager, appUpdateInfo,
                    if (numberOfFailedOrCancelledAttempts >= FoodsGullyConstants.MAX_ALLOWED_ATTEMPTS) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE)
            }
        }
    }


    private fun updateApp(
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo,
        appUpdateType: Int
    ) {

        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            appUpdateType,
            this,
            FoodsGullyConstants.APP_UPDATE_REQUEST_CODE
        )
    }


    private fun getNumberOfUpdatedFailedOrCancelled() : Int {
        return SharedPreferenceManager(this, FoodsGullyConstants.LOGIN_SP).getIntegerPreference(
            FoodsGullyConstants.KEY_NO_FAILED_UPDATES,
            0
        )
    }

    private fun updateNumberOfFailedOrCancelledUpdated(count : Int) {
        SharedPreferenceManager(this,FoodsGullyConstants.LOGIN_SP).storeIntegerPreference(FoodsGullyConstants.KEY_NO_FAILED_UPDATES,count)
    }

    private fun setupToolbar() {
        toolbar = bindingMainActivity.appBar.toolbar
        setSupportActionBar(toolbar)
    }


    private fun initializeViews() {

        cartLayout = bindingMainActivity.appBar.contentMain.layoutProceedToCart
        cartBadgeView = bindingMainActivity.appBar.ivCart

        drawerLayout = bindingMainActivity.drawerLayout
        val headerLayout = bindingMainActivity.navView.getHeaderView(0)
        tvProfileName = headerLayout.findViewById(R.id.tvProfileName)
        ivUserImage = headerLayout.findViewById(R.id.ivProfileImage)

        navController = findNavController(R.id.nav_host_fragment)


        initializeNavigationDrawer()

        initializeListeners()

    }


    private fun initializeUserInfo() {

        currentUser = FoodsGullyUtils.getCurrentUser(this)
        tvProfileName.text = currentUser?.fullName ?: ""

       if (!currentUser?.imageUrl.isNullOrEmpty()) {

            FirebaseFileUploadService(this).downloadFile(
                currentUser?.imageUrl ?: "",
                3,
                this,
                FileType.USER_IMAGE
            )
        }

    }

    private fun initializeListeners() {
        cartBadgeView.setOnClickListener { goToCartScreen() }

        cartLayout.setOnClickListener { goToCartScreen() }

        navView.setNavigationItemSelectedListener { item ->
            launchSelectedNavigationDrawerFragment(item)
        }
    }


    override fun onResume() {
        super.onResume()
        showOrHideCartLayout(FoodsGullyUtils.getItemsInCart(this))
        initializeUserInfo()
    }

    private fun launchSelectedNavigationDrawerFragment(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.nav_menu_call -> callForDelivery()

            R.id.nav_menu_order_history -> navController.navigate(R.id.nav_order_history)

            R.id.nav_menu_account -> navController.navigate(
                R.id.nav_profile,
                bundleOf(FoodsGullyConstants.USER_DATA to currentUser)
            )

            R.id.nav_menu_location -> navController.navigate(
                R.id.nav_address,
                bundleOf(FoodsGullyConstants.USER_DATA to currentUser)
            )

            R.id.nav_menu_logout -> logout()

            else -> navController.navigate(R.id.nav_home)
        }

        closeDrawer()

        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mSearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager


        return super.onCreateOptionsMenu(menu)
    }

    private fun logout() {
        SharedPreferenceManager(context = this, name = FoodsGullyConstants.LOGIN_SP).clearAll()
        SharedPreferenceManager(this, name = FoodsGullyConstants.CATEGORIES_SP).clearAll()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun callForDelivery() {
        FoodsGullyUtils.callForDelivery(this)
    }

    private fun goToCartScreen() {
        navController.navigate(R.id.nav_cart)
    }


    private fun initializeNavigationDrawer() {

        navView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_menu_account,
                R.id.nav_menu_location,
                R.id.nav_menu_order_history,
                R.id.nav_menu_call,
                R.id.nav_menu_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getProceedToCartLayout() = cartLayout

    fun showOrHideCartLayout(cartItems: ArrayList<CartItem>) {
        cartLayout.visibility = if (cartItems.isNotEmpty()) View.VISIBLE else View.GONE
        cartLayout.tvItemTotal.text =
            "${getString(R.string.rupee_symbol)} ${cartItems.map { it.itemPrice }.sum()}"
        val totalQuantity = cartItems.map { it.totalQuantity }.sum()
        cartLayout.tvTotalItemsInCart.text =
            totalQuantity.toString() + " " + if (totalQuantity < 1) getString(R.string.txt_item) else getString(
                R.string.txt_items
            )
        updateCartBadge(totalQuantity)
    }

    fun updateCartBadge(value: Int) {
        cartBadgeView.badgeValue = value
    }

    fun hideToolbar(toHide: Boolean) {
        toolbar.visibility = if (toHide) View.GONE else View.VISIBLE
    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        if (!isDestroyed) {
            FoodsGullyUtils.setUserImageToView(
                ivUserImage,
                fileUri,
                this,
                R.drawable.ic_person_gray,
                0.2f,
                currentUser
            )
        }
    }

    override fun onFileDownloadFailed(fileType: FileType) {
        if (!isDestroyed) {
            FoodsGullyUtils.setImageToView(
                ivUserImage,
                Uri.EMPTY, this, R.drawable.ic_person_gray, 0.2f
            )
        }
    }

    fun showOrHideCartLayout(toShow: Boolean) {
        cartLayout.visibility = if (toShow) View.VISIBLE else View.GONE
    }


    override fun onBackPressed() {

    }

    private fun closeDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT)
    }

    override fun onPaymentError(errorCode: Int, response: String?) {

        Log.i("TAG", "error $response")
        FoodsGullyUtils.showSnackbar(
            getString(R.string.payment_unsuccessful),
            bindingMainActivity.root
        )

    }

    override fun onPaymentSuccess(paymentId: String?) {

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->

                if (fragment is CheckoutFragment) {
                    fragment.onPaymentSuccess(paymentId)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                when (fragment) {
                    is AddressFragment -> fragment.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                    )
                    is ProfileFragment -> fragment.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                    )
                }

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == FoodsGullyConstants.APP_UPDATE_REQUEST_CODE && (resultCode != Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED)) {
            updateNumberOfFailedOrCancelledUpdated(getNumberOfUpdatedFailedOrCancelled() + 1)

        } else {

            val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            navHost?.let { navFragment ->
                navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                    when (fragment) {
                        is ProfileFragment -> fragment.onActivityResult(
                            requestCode,
                            resultCode,
                            data
                        )
                    }

                }
            }
        }
    }


    fun getSearchManager() = mSearchManager



}
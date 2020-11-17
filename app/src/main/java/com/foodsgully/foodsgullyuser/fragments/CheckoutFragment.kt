package com.foodsgully.foodsgullyuser.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.foodsgully.foodsgullyuser.BuildConfig
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.CheckoutFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.CheckoutViewModel
import com.foodsgully.foodsgullyuser.viewmodels.PaymentMode
import com.foodsgully.foodsgullyuser.viewmodels.PaymentStatus
import com.foodsgully.foodsgullyuser.viewmodels.TimeRangeForDelivery
import com.foodsgully.foodsgullyuser.viewmodels.factories.CheckoutViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import java.util.*


class CheckoutFragment : Fragment() {

    private lateinit var bindingCheckoutFragment: CheckoutFragmentBinding

    private var mProgressDialog: AlertDialog? = null
    private var checkoutViewModel: CheckoutViewModel? = null
    private var finalOrderAmount: Double = 0.0


    private val dateOptions = arrayOf("Today", "Tomorrow").toMutableList()

    companion object {
        fun newInstance() = CheckoutFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            finalOrderAmount = getDouble(FoodsGullyConstants.ARG_FINAL_AMOUNT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCheckoutFragment =
            DataBindingUtil.inflate(inflater, R.layout.checkout_fragment, container, false)
        bindingCheckoutFragment.lifecycleOwner = viewLifecycleOwner
        requireActivity().viewModelStore.clear()
        addOtherDatesOfTheWeekToDateOptions()
        return bindingCheckoutFragment.root
    }

    private fun addOtherDatesOfTheWeekToDateOptions() {
        for (i in 2 until 7) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, i)
            dateOptions.add(
                i,
                FoodsGullyUtils.getDateString(
                    calendar.time,
                    FoodsGullyConstants.DISPLAY_DATE_FORMAT
                ).toString()
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkoutViewModel =
            CheckoutViewModelFactory(finalOrderAmount).getViewModel(true, requireActivity())

        initializeSpinner()
        initializePlaceOrderData()
        initializeListeners()
    }


    override fun onResume() {
        super.onResume()
        hideProceedToLayout(toHide = true)
    }


    private fun hideProceedToLayout(toHide: Boolean) {
        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(!toHide)
        }
    }

    private fun initializePlaceOrderData() {
        checkoutViewModel?.getCartItems()?.clear()
        checkoutViewModel?.getCartItems()?.addAll(FoodsGullyUtils.getItemsInCart(requireContext()))

        checkoutViewModel?.getDeliveryAddress()?.value =
            FoodsGullyUtils.getDeliveryAddress(requireContext())
    }

    private fun initializeSpinner() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dateOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bindingCheckoutFragment.spDate.adapter = adapter


    }

    private fun initializeListeners() {

        bindingCheckoutFragment.spDate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    handleDateSelection(position)
                }

            }

        bindingCheckoutFragment.rgTime.setOnCheckedChangeListener { _, checkedId ->
            handleTimeSelection(checkedId)
        }

        bindingCheckoutFragment.rgPaymentMode.setOnCheckedChangeListener { _, checkedId ->
            handlePaymentModeSelection(checkedId)
        }

        bindingCheckoutFragment.btnPlaceOrder.setOnClickListener { placeOrder() }

        bindingCheckoutFragment.ibBack.setOnClickListener { goToCartScreen() }


    }

    private fun goToCartScreen() {
        findNavController().popBackStack(R.id.nav_cart, false)
    }

    private fun handlePaymentModeSelection(checkedId: Int) {
        when (checkedId) {
            R.id.rbPayonline -> {
                checkoutViewModel?.getPaymentMode()?.value = PaymentMode.ONLINE.paymentMode
            }
            else -> {
                checkoutViewModel?.getPaymentMode()?.value = PaymentMode.CASH.paymentMode
                checkoutViewModel?.getPaymentStatus()?.value = PaymentStatus.NOT_PAID.status
            }
        }
    }

    private fun placeOrder() {

        if (checkoutViewModel?.validateTimeRange() == false) {
            FoodsGullyUtils.showSnackbar(
                getString(R.string.select_delivery_time),
                bindingCheckoutFragment.root
            )
            return
        }

        if (checkoutViewModel?.validateHomeAddress() == false) {
            showSnackbarWithAction()
            return
        }

        if(!FoodsGullyUtils.checkIfDeliverableLocation(checkoutViewModel?.getDeliveryAddress()?.value?.latitude ?: -91.0, checkoutViewModel?.getDeliveryAddress()?.value?.longitude ?: -181.0) ) {
            FoodsGullyUtils.showSnackbar(getString(R.string.not_deliverable_location),bindingCheckoutFragment.root)
            return
        }

        if(checkoutViewModel?.isPaymentModeOnline() == true) launchPaymentGateway() else createOrder()


    }

    private fun showSnackbarWithAction() {
        val snackbar = Snackbar.make(
            bindingCheckoutFragment.root,
            getString(R.string.fill_home_address),
            Snackbar.LENGTH_SHORT
        )

        snackbar.setAction("Click Here", View.OnClickListener { goToAddressScreen() })
        snackbar.show()
    }

    private fun goToAddressScreen() {
        findNavController().navigate(R.id.nav_address)
    }

    private fun createOrder() {

        checkoutViewModel?.placeOrder(requireContext())
            ?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                when (response) {

                    is APILoader -> {
                        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                            getString(R.string.placing_the_order),
                            requireContext()
                        )
                    }

                    is APIError -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            response.errorMessage,
                            bindingCheckoutFragment.root
                        )
                    }

                    is Success<*> -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            getString(R.string.order_placed),
                            bindingCheckoutFragment.root
                        )

                        clearCartItems()
                        goToHomeScreen()

                    }
                }
            })

    }

    private fun clearCartItems() {
        FoodsGullyUtils.clearCartItems(requireContext())
    }

    private fun goToHomeScreen() {
        findNavController().popBackStack(R.id.nav_home, false)
    }


    private fun launchPaymentGateway() {

        val paymentCheckout = Checkout()
        paymentCheckout.setImage(R.drawable.foodsgully_logo)
        val currentUser = FoodsGullyUtils.getCurrentUser(requireContext())
        try {
            val options = JSONObject()
            options.put("name", getString(R.string.name_foodsgully))
            options.put("description", "Order payment")
            options.put("theme.color", "#D62E2E")
            options.put("currency", "INR")
            options.put(
                "amount",
                ((checkoutViewModel?.getTotalAmount() ?: 0.0) * 100.0).toInt()
            ) //pass amount in currency subunits
            options.put("prefill.email", currentUser?.email)
            options.put("prefill.contact", currentUser?.phoneNumber)
            paymentCheckout.open(requireActivity(), options)
        } catch (e: Exception) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e)
        }


    }

    private fun handleTimeSelection(checkedId: Int) {
        checkoutViewModel?.getDeliveryTime()?.value =
            when (checkedId) {
                R.id.rbEarlyMorning -> bindingCheckoutFragment.rbEarlyMorning.text.toString()

                R.id.rbMorning -> bindingCheckoutFragment.rbMorning.text.toString()

                R.id.rbAfternoon -> bindingCheckoutFragment.rbAfternoon.text.toString()

                else -> bindingCheckoutFragment.rbEvening.text.toString()
            }
    }

    private fun handleDateSelection(position: Int) {
        when (position) {
            0 -> {
                checkoutViewModel?.getDeliveryDate()?.value = FoodsGullyUtils.getDateString(
                    Calendar.getInstance().time,
                    FoodsGullyConstants.POST_DATE_FORMAT
                )
                handleTimeRangeSelection(true)
            }

            1 -> {
                checkoutViewModel?.getDeliveryDate()?.value = getTomorrowDate()
                handleTimeRangeSelection(false)
            }

            else -> {
                val displayDate = dateOptions[2]
                checkoutViewModel?.getDeliveryDate()?.value = FoodsGullyUtils.convertDate(
                    displayDate,
                    FoodsGullyConstants.DISPLAY_DATE_FORMAT,
                    FoodsGullyConstants.POST_DATE_FORMAT
                )
                handleTimeRangeSelection(false)
            }
        }
    }

    private fun getTomorrowDate(): String? {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        return FoodsGullyUtils.getDateString(calendar.time, FoodsGullyConstants.POST_DATE_FORMAT)
    }

    private fun handleTimeRangeSelection(isToday: Boolean) {
        val eligibleTimeRanges =
            if (isToday) checkoutViewModel?.getEligibleTimePeriodForToday() else {
                checkoutViewModel?.getInEligibleTimePeriodForDay()?.clear()
                arrayListOf<TimeRangeForDelivery>(
                    TimeRangeForDelivery.EARLY_MORNING,
                    TimeRangeForDelivery.MORNING,
                    TimeRangeForDelivery.AFTERNOON,
                    TimeRangeForDelivery.EVENING
                )
            }

        val iterator = eligibleTimeRanges?.iterator()
        while (iterator != null && iterator.hasNext()) {
            val timeRangeForDelivery = iterator.next()
            enableTimeRanges(timeRangeForDelivery)
        }

        handleIneligibleTimeRangeSelection()

        if (eligibleTimeRanges?.isEmpty() == true && isToday) {
            bindingCheckoutFragment.spDate.setSelection(1)
            FoodsGullyUtils.showSnackbar(
                getString(R.string.no_delivery_time_for_today),
                bindingCheckoutFragment.root
            )
        }


    }


    private fun handleIneligibleTimeRangeSelection() {
        val inEligibleTimeRanges = checkoutViewModel?.getInEligibleTimePeriodForDay()
        val iterator = inEligibleTimeRanges?.iterator()
        while (iterator != null && iterator.hasNext()) {
            val timeRangeForDelivery = iterator.next()
            disableTimeRange(timeRangeForDelivery)
        }

    }

    private fun enableTimeRanges(timeRangeForDelivery: TimeRangeForDelivery) {

        when (timeRangeForDelivery) {
            TimeRangeForDelivery.EARLY_MORNING -> {
                bindingCheckoutFragment.rbEarlyMorning.isEnabled = true
                bindingCheckoutFragment.tvLblEarlyMorning.isEnabled = true
            }

            TimeRangeForDelivery.MORNING -> {
                bindingCheckoutFragment.rbMorning.isEnabled = true
                bindingCheckoutFragment.tvLblMorning.isEnabled = true
            }

            TimeRangeForDelivery.AFTERNOON -> {
                bindingCheckoutFragment.rbAfternoon.isEnabled = true
                bindingCheckoutFragment.lblAfternoon.isEnabled = true
            }

            TimeRangeForDelivery.EVENING -> {
                bindingCheckoutFragment.rbEvening.isEnabled = true
                bindingCheckoutFragment.lblEvening.isEnabled = true
            }
        }
    }


    private fun disableTimeRange(timeRangeForDelivery: TimeRangeForDelivery) {

        when (timeRangeForDelivery) {
            TimeRangeForDelivery.EARLY_MORNING -> {
                bindingCheckoutFragment.rbEarlyMorning.isEnabled = false
                bindingCheckoutFragment.tvLblEarlyMorning.isEnabled = false
            }

            TimeRangeForDelivery.MORNING -> {
                bindingCheckoutFragment.rbMorning.isEnabled = false
                bindingCheckoutFragment.tvLblMorning.isEnabled = false
            }

            TimeRangeForDelivery.AFTERNOON -> {
                bindingCheckoutFragment.rbAfternoon.isEnabled = false
                bindingCheckoutFragment.lblAfternoon.isEnabled = false
            }

            TimeRangeForDelivery.EVENING -> {
                bindingCheckoutFragment.rbEvening.isEnabled = false
                bindingCheckoutFragment.lblEvening.isEnabled = false
            }
        }
    }


    private fun updatePaymentId(paymentId: String?, paymentSuccess: Boolean) {
        checkoutViewModel?.updatePaymentStatus(paymentId, paymentSuccess, requireContext())
            ?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->

                when (response) {

                    is APILoader -> {
                        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                            getString(R.string.updating_payment_status),
                            requireContext()
                        )
                    }

                    is APIError -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            response.errorMessage,
                            bindingCheckoutFragment.root
                        )
                    }

                    is Success<*> -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            getString(R.string.payment_successful),
                            bindingCheckoutFragment.root
                        )

                        clearCartItems()
                        goToHomeScreen()

                    }
                }
            })
    }

    fun onPaymentSuccess(paymentId: String?) {
        checkoutViewModel?.setPaymentIdAndStatus(paymentId,PaymentStatus.PAID.status)
        createOrder()
    }


}
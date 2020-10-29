package com.foodsgully.foodsgullyuser.utils

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.niro.niroapp.database.SharedPreferenceManager

object FoodsGullyUtils {

    @JvmStatic
    fun showSnackbar(message: String, root: View) {

        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showLoaderProgress(message: String, context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message
        progressDialog.show(
        return progressDialog
    }

    @JvmStatic
    fun getToken(context: Context): String? {
        return "Bearer " + SharedPreferenceManager(
            context,
            NiroAppConstants.LOGIN_SP
        ).getStringPreference(NiroAppConstants.USER_TOKEN)
    }


    @JvmStatic
    fun getDefaultErrorMessage(context: Context?): String {

        return context?.getString(R.string.something_went_wrong) ?: NiroAppConstants.SWW
    }
}
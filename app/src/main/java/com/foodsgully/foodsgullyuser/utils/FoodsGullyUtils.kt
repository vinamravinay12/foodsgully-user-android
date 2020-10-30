package com.foodsgully.foodsgullyuser.utils


import android.content.Context
import android.view.View
import carbon.dialog.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.niro.foodsgullyuser.R

import com.niro.niroapp.database.SharedPreferenceManager

object FoodsGullyUtils {

    @JvmStatic
    fun showSnackbar(message: String, root: View) {

        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showLoaderProgress(message: String, context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setText(message)
        progressDialog.show()
        return progressDialog
    }

    @JvmStatic
    fun dismissProgress(progressDialog: ProgressDialog?) {
        if(progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()
    }

    @JvmStatic
    fun getToken(context: Context): String? {
        return "Bearer " + SharedPreferenceManager(
            context,
            FoodsGullyConstants.LOGIN_SP
        ).getStringPreference(FoodsGullyConstants.USER_TOKEN)
    }


    @JvmStatic
    fun getDefaultErrorMessage(context: Context?): String {

        return context?.getString(R.string.something_went_wrong) ?: FoodsGullyConstants.SWW
    }
}
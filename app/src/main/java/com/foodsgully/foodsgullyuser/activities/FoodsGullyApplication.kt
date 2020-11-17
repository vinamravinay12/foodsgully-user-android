package com.foodsgully.foodsgullyuser.activities

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.Nullable
import com.livefront.bridge.Bridge
import com.livefront.bridge.SavedStateHandler
import com.livefront.bridge.ViewSavedStateHandler
import icepick.Icepick


class FoodsGullyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Bridge.initialize(applicationContext, object : SavedStateHandler {
            override fun saveInstanceState(target: Any, state: Bundle) {
                Icepick.saveInstanceState(target, state)
            }

            override fun restoreInstanceState(
                target: Any,
                @Nullable state: Bundle?
            ) {
                Icepick.restoreInstanceState(target, state)
            }
        }, object: ViewSavedStateHandler {
            override fun <T : View?> saveInstanceState(
                target: T,
                parentState: Parcelable?
            ): Parcelable {
                return Icepick.saveInstanceState(target, parentState);
            }

            override fun <T : View?> restoreInstanceState(
                target: T,
                state: Parcelable?
            ): Parcelable? {
                return Icepick.restoreInstanceState(target, state);
            }

        })
    }

}
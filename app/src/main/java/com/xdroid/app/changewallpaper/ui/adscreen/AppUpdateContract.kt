package com.xdroid.app.changewallpaper.ui.adscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType


//
//class AppUpdateContract : ActivityResultContract<IntentSenderRequest, Boolean>() {
//    override fun createIntent(context: Context, input: IntentSenderRequest): Intent {
//        return input.fillInIntent!!
//    }
//
//    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
//        return resultCode == Activity.RESULT_OK
//    }
//}
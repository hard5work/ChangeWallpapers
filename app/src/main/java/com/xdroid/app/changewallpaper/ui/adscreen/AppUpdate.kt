package com.xdroid.app.changewallpaper.ui.adscreen

//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.IntentSender
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContract
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.platform.LocalContext
//import com.google.android.play.core.appupdate.AppUpdateInfo
//import com.google.android.play.core.appupdate.AppUpdateManager
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.appupdate.AppUpdateOptions
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.UpdateAvailability
//import com.google.android.play.core.ktx.requestAppUpdateInfo
//import kotlinx.coroutines.launch

//
//@Composable
//fun CheckForAppUpdate(
//    onUpdateFailed: (Exception) -> Unit = {},
//    onUpdateComplete: () -> Unit = {}
//) {
//    val context = LocalContext.current
//    val appUpdateManager = remember { AppUpdateManagerFactory.create(context) }
//    val coroutineScope = rememberCoroutineScope()
//
//    // Registering the ActivityResultLauncher for handling the update result
//    val activityResultLauncher = rememberLauncherForActivityResult(
//        contract = AppUpdateContract(),
//        onResult = { success ->
//            if (success) {
//                onUpdateComplete()
//            } else {
//                onUpdateFailed(Exception("Update failed or canceled"))
//            }
//        }
//    )
//
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            try {
//                // Request app update info using the KTX API
//                val appUpdateInfo = appUpdateManager.requestAppUpdateInfo()
//
//                // Check if an update is available
//                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                    // Start the update flow if an update is available
//                    appUpdateManager.startUpdateFlowForResult(
//                        appUpdateInfo,
//                        activityResultLauncher,
//                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
//
//                }
//            } catch (e: Exception) {
//                onUpdateFailed(e)
//            }
//        }
//    }
//}
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
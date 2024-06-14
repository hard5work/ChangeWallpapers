package com.xdroid.app.changewallpaper.utils.helpers

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


fun Boolean?.isNotNull(): Boolean {
    return this != null
}

fun Boolean?.isNull(): Boolean {
    return this ?: false
}

fun Float?.isNull(): Float {
    return this ?: 0f
}

fun getMin(data: Long?): String {
    return if (data == null) {
        ""
    } else {
        "$data min."
    }
}

fun String.empty(): Boolean = this.trim().isEmpty()
fun String.notEmpty(): Boolean = this.trim().isNotEmpty()

fun String.isRequired() = "$this is required."
fun String?.isNull() = if (this.isNullOrEmpty()) "" else this


fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}

@Composable
fun ScrollableScreen(
    content: @Composable () -> Unit
) {
    val imeHeight = remember { mutableStateOf(0) }
    val isKeyboardVisible = remember { mutableStateOf(false) }

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val onFocusChanged: (FocusState) -> Unit = { focusState ->
        isKeyboardVisible.value = focusState.isFocused

        if (focusState.isFocused) {
            imeHeight.value = 500 ?: 0
        } else {
            imeHeight.value = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = imeHeight.value.dp)
            .onFocusChanged(onFocusChanged)
    ) {
        content()
    }
}

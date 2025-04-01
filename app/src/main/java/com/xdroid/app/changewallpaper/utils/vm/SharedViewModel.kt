package com.xdroid.app.changewallpaper.utils.vm

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xdroid.app.changewallpaper.cmodel.ListItems

// ViewModel to Store Data
class SharedViewModel : ViewModel() {
    var jsonData: ListItems? = null

    // Convert Object to JSON
    fun setUserData(user: ListItems) {
        jsonData = user
    }

    // Convert JSON back to Object
    fun getUserData(): ListItems? {
        return jsonData
    }
}
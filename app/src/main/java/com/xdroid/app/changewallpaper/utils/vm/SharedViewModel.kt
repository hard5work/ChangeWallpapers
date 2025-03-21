package com.xdroid.app.changewallpaper.utils.vm

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xdroid.app.changewallpaper.cmodel.ListItems

// ViewModel to Store Data
class SharedViewModel : ViewModel() {
    var jsonData: String? = null

    // Convert Object to JSON
    fun setUserData(user: ListItems) {
        jsonData = Gson().toJson(user)
    }

    // Convert JSON back to Object
    fun getUserData(): ListItems? {
        return jsonData?.let { Gson().fromJson(it, ListItems::class.java) }
    }
}
package com.xdroid.app.changewallpaper.utils.enums



enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    IDLE
}


data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val title: String?
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> error(msg: String, title: String = "Error"): Resource<T> {
            return Resource(Status.ERROR, null, msg, title)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null, null)
        }

        fun <T> idle(): Resource<T> {
            return Resource(Status.IDLE, null, null, null)
        }
    }
}

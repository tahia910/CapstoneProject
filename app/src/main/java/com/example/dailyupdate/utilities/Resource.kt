package com.example.dailyupdate.utilities

import com.example.dailyupdate.utilities.Status.ERROR
import com.example.dailyupdate.utilities.Status.LOADING
import com.example.dailyupdate.utilities.Status.SUCCESS

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(val status: Status,
                           val data: T?,
                           val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(LOADING, data, null)
        }
    }
}
package com.example.dailyupdate.networking

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.dailyupdate.utilities.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

abstract class NetworkBoundResource<ResultType, RequestType>(scope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        scope.launch {
            result.value = Resource.loading(null)
            fetchFromNetwork()
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private suspend fun fetchFromNetwork() {
        try {
            val result = withContext(Dispatchers.IO) {
                val apiResponse = createCall()
                val parsedResponse = processResponse(apiResponse)
                saveCallResult(parsedResponse)
                parsedResponse
            }
            setValue(Resource.success(result))
        } catch (e: Exception) {
            e.printStackTrace()
            setValue(Resource.error(customErrorMsg() ?: "Error", null))
        }


    }

//    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected abstract fun processResponse(response: RequestType): ResultType

    @WorkerThread
    open fun saveCallResult(item: ResultType){}

//    @MainThread
//    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract suspend fun createCall(): RequestType

    open fun customErrorMsg(): String? {
        return null
    }
}

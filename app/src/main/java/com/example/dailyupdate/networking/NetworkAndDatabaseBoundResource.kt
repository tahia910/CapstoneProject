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

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class NetworkAndDatabaseBoundResource<ResultType, RequestType>
(scope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        scope.launch {
            result.value = Resource.loading(null)
            @Suppress("LeakingThis")
            val dbSource = loadFromDb()
            result.addSource(dbSource) { data ->
                result.removeSource(dbSource)
//                if (shouldFetch(data)) {
//                    fetchFromNetwork(dbSource)
//                } else {
//                    result.addSource(dbSource) { newData ->
                setValue(Resource.loading(data))
//                    }
//                }
            }
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
            withContext(Dispatchers.IO) {
                val apiResponse = createCall()
                saveCallResult(processResponse(apiResponse))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setValue(Resource.error(customErrorMsg() ?: "Error", null))
        }

        result.addSource(loadFromDb()) { newData ->
            setValue(Resource.success(newData))
        }
    }

//    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected abstract fun processResponse(response: RequestType): ResultType

    @WorkerThread
    protected abstract fun saveCallResult(item: ResultType)

//    @MainThread
//    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract suspend fun createCall(): RequestType

    open fun customErrorMsg(): String? {
        return null
    }
}
package com.test.dynamicdemo.util

import android.util.Log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Coroutines {
    private const val TAG:String="Coroutines"
    fun main(work: suspend (()-> Unit)){
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }
    }

    //need to learn and apply
    fun<T:Any> ioThenMain(
        work:suspend (()->T?),
        callback:((T?)->Unit),
        exception: (String?) -> Unit
    )=
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val data = CoroutineScope(Dispatchers.IO).async rt@{
                    Log.d(TAG, "IO processing")
                    return@rt work()
                }.await()
                Log.d(TAG, "main processing")
                callback(data)
            }catch (e: ApiExceptions){
                if ((e?.message != null)&&(e.message.contains("unKnownHostException",true))){
                    exception("You are not connected to network.")
                    return@launch
                    }
                Log.d("exception",e.toString())
                exception(e.message)
            }
        }
}
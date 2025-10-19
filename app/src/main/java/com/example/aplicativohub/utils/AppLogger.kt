package com.example.aplicativohub.utils

import android.util.Log

object AppLogger {

    private const val TAG = "HubApp"

    fun v(message: String) = Log.v(TAG, message)
    fun d(message: String) = Log.d(TAG, message)
    fun i(message: String) = Log.i(TAG, message)
    fun w(message: String) = Log.w(TAG, message)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
    fun wtf(message: String, throwable: Throwable? = null) = Log.wtf(TAG, message, throwable)
}

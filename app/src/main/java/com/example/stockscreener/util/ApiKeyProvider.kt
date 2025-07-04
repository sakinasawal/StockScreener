package com.example.stockscreener.util

object ApiKeyProvider {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getApiKey() :String
}
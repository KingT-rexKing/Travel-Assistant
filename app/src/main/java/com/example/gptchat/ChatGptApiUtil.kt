package com.example.gptchat

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object ChatGptApiUtil {
    private val apiKey: String = ""
    private val apiUrl = "https://api.openai.com/v1/chat/completions"
    private val uiThread = Handler(Looper.getMainLooper());
    fun callChatGptApi(prompt: String, callback: ChatGptCallback) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()


        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonBody = JSONObject()
        jsonBody.put("model", "gpt-3.5-turbo")
        jsonBody.put("messages", JSONArray().apply {
//            put(JSONObject().apply {
//                put("role", "system")
//                put("content", "You are a helpful assistant.")
//            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        })

        val requestBody =
            RequestBody.create(mediaType, jsonBody.toString())

        val request = Request.Builder().url(apiUrl).post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json").build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                uiThread.post {
                    callback.onError("Network error")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    uiThread.post { callback.onError("Request failed" + response.code + "_" + response.message) }
                } else {
                    try {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody)
                        val completion =
                            jsonObject.getJSONArray("choices").getJSONObject(0)
                                .getJSONObject("message").getString("content")
                        uiThread.post {
                            callback.onSuccess(completion)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.onError("JSON parsing error")
                    }
                }
            }
        })
    }

    interface ChatGptCallback {
        fun onSuccess(response: String)
        fun onError(error: String)
    }
}

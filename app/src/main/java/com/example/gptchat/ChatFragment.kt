package com.example.gptchat

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gptchat.databinding.ActivityMainBinding

class ChatFragment : Fragment() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding.apply {
            val chatAdapter = ChatAdapter()
            rvChat.adapter = chatAdapter
            ivVoice.setOnClickListener {
                val text = etText.text.toString()
                if (text.isEmpty()) {
                    return@setOnClickListener
                }
                etText.setText("")
                chatAdapter.addData(ChatData(text, 1))
                ChatGptApiUtil.callChatGptApi(text, object : ChatGptApiUtil.ChatGptCallback {
                    override fun onSuccess(response: String) {
                        chatAdapter.addData(ChatData(response, 0))
                    }

                    override fun onError(error: String) {
                        chatAdapter.addData(ChatData(error, 0))

                    }
                })

            }
            etText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.action) {
                    val text = etText.text.toString()
                    if (text.isEmpty()) {
                        return@setOnKeyListener true
                    }
                    etText.setText("")
                    chatAdapter.addData(ChatData(text, 1))
                    ChatGptApiUtil.callChatGptApi(text, object : ChatGptApiUtil.ChatGptCallback {
                        override fun onSuccess(response: String) {
                            chatAdapter.addData(ChatData(response, 0))
                        }
                        override fun onError(error: String) {
                            chatAdapter.addData(ChatData(error, 0))
                        }
                    })


                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

        }
        return binding.root
    }


}
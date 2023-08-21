package com.example.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TypeWriterTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    private var typingMessage: String = ""
    private var messageIndex: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    private val typingRunnable = object : Runnable {
        override fun run() {
            if (messageIndex < typingMessage.length) {
                append(typingMessage[messageIndex].toString())
                messageIndex++
                handler.postDelayed(this, 30)
            }
        }
    }

    fun startTypingText(message: String) {
        this.typingMessage = message
        this.messageIndex = 0
        handler.post(typingRunnable)
    }

    fun stopTyping() {
        handler.removeCallbacks(typingRunnable)
    }

    fun reset() {
        stopTyping()
        text = ""
    }

}

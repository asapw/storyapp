package com.example.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var isFocusedInternally = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isFocusedInternally) {
                    if (s != null && s.length < 8) {
                        error = "Password must be at least 8 characters"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Add focus change listener
        setOnFocusChangeListener { _, hasFocus ->
            isFocusedInternally = hasFocus
            if (!hasFocus) {
                if (text?.length ?: 0 < 8) {
                    error = "Password must be at least 8 characters"
                }
            }
        }
    }
}

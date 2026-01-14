package com.dating.auth.presentation.register

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

@androidx.compose.foundation.ExperimentalFoundationApi
object DateInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        val currentText = asCharSequence().toString()
        
        // 1. Filter out non-digits
        val digits = currentText.filter { it.isDigit() }
        
        // 2. Limit to 8 digits (DDMMYYYY)
        val truncated = if (digits.length > 8) digits.subSequence(0, 8) else digits
        
        // 3. Format as DD/MM/YYYY
        val formatted = StringBuilder()
        for (i in truncated.indices) {
            formatted.append(truncated[i])
            if ((i == 1 || i == 3) && i != truncated.lastIndex) {
               formatted.append('/')
            }
        }
        
        val newText = formatted.toString()
        
        // Only replace if content is different to avoid potential loops or redundant updates
        if (currentText != newText) {
             replace(0, length, newText)
        }
    }
}

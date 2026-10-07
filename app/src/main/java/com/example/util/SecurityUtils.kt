package com.example.util

import java.security.MessageDigest
import java.util.regex.Pattern

object SecurityUtils {

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_PATTERN.matcher(email.trim()).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return cleaned.length in 7..15
    }

    fun isPasswordStrong(password: String): Boolean {
        // At least 6 chars, contains letters and numbers or symbols
        if (password.length < 6) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() || !it.isLetterOrDigit() }
        return hasLetter && hasDigit
    }
}

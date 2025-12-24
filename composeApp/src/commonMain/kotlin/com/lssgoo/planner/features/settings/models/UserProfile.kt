package com.lssgoo.planner.features.settings.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.util.KmpIdGenerator
import com.lssgoo.planner.util.KmpTimeUtils
// For date handling, we can use Kotlinx datetime or simplified logic in Greeting generator
// import kotlinx.datetime.* 

/**
 * User profile data collected during onboarding
 */
@Serializable
data class UserProfile(
    val id: String = KmpIdGenerator.generateId(),
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: Long? = null,
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUri: String = "",
    val occupation: String = "",
    val isOnboardingComplete: Boolean = false,
    val createdAt: Long = KmpTimeUtils.currentTimeMillis(),
    val updatedAt: Long = KmpTimeUtils.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
    
    val age: Int?
        get() = dateOfBirth?.let {
            val now = KmpTimeUtils.currentTimeMillis()
            val diff = now - it
            (diff / (365.25 * 24 * 60 * 60 * 1000)).toInt()
        }
    
    // Greeting logic moved to a utility or simple check if KTX-datetime isn't fully setup in file
    // Simplified greeting for now to avoid Calendar dependency here
    fun getGreeting(hour: Int): String {
        val timeGreeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        return if (firstName.isNotBlank()) {
            val genderSuffix = when (gender) {
                Gender.MALE -> "Sir"
                Gender.FEMALE -> "Ma'am"
                else -> firstName
            }
            "$timeGreeting, $genderSuffix!"
        } else {
            "$timeGreeting!"
        }
    }
}

/**
 * Gender options
 */
@Serializable
enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    NON_BINARY("Non-Binary"),
    PREFER_NOT_TO_SAY("Prefer not to say")
}

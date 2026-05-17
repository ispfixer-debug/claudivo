package com.vito.core.domain.model

import kotlinx.serialization.Serializable

/**
 * User types in Vito platform.
 * Per PLAN.md §10
 */
@Serializable
enum class UserRole {
    CLIENT,
    DRIVER,
    ADMIN,
    SUPER_ADMIN
}

/**
 * User profile - shared across all user types.
 * Per PLAN.md §4 and §10
 */
@Serializable
data class User(
    val id: String,
    val phone: String,
    val displayName: String?,
    val role: UserRole,
    val language: String = "en",
    val fcmToken: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean = true
)

/**
 * Client-specific profile fields.
 */
@Serializable
data class ClientProfile(
    val userId: String,
    val email: String? = null,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val walletBalanceCents: Long = 0,
    val referrerCode: String? = null,
    val referredByCode: String? = null,
    val totalTrips: Int = 0
)

/**
 * Driver-specific profile fields.
 * Per PLAN.md §11 - KYC and vehicle info
 */
@Serializable
data class DriverProfile(
    val userId: String,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val walletBalanceCents: Long = 0,
    val carMake: String,
    val carModel: String,
    val carColor: String,
    val carPlate: String,
    val carYear: Int,
    val kycStatus: KycStatus = KycStatus.NOT_STARTED,
    val stripeAccountId: String? = null,
    val stripeOnboardingComplete: Boolean = false,
    val isOnline: Boolean = false,
    val driverReferralCode: String
)

/**
 * KYC document status.
 * Per PLAN.md §11.1
 */
@Serializable
enum class KycStatus {
    NOT_STARTED,
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED
}

/**
 * KYC document.
 * Per PLAN.md §11.1 - three documents required
 */
@Serializable
data class KycDocument(
    val id: String,
    val driverId: String,
    val type: KycDocumentType,
    val imageUrl: String?,
    val status: KycStatus,
    val rejectionReason: String? = null,
    val submittedAt: Long? = null,
    val reviewedAt: Long? = null
)

@Serializable
enum class KycDocumentType {
    GOVERNMENT_ID,
    DRIVERS_LICENSE,
    VEHICLE_INSURANCE
}

/**
 * Admin-specific profile.
 */
@Serializable
data class AdminProfile(
    val userId: String,
    val canManageDrivers: Boolean = false,
    val canManageClients: Boolean = false,
    val canViewFinance: Boolean = false,
    val canManageFeatureFlags: Boolean = false,
    val canManageAdmins: Boolean = false
)
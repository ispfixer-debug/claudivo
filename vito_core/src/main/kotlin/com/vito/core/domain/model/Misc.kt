package com.vito.core.domain.model

import kotlinx.serialization.Serializable

/**
 * QR Token - for driver onboarding and client referrals.
 * Per PLAN.md §18
 */
@Serializable
data class QrToken(
    val id: String,
    val token: String,
    val kind: QrTokenKind,
    val createdByUserId: String?,
    val targetUserId: String? = null,
    val isActive: Boolean = true,
    val expiresAt: Long,
    val redeemedAt: Long? = null,
    val scanCount: Int = 0,
    val maxScans: Int = 1, // 1 for onboarding, unlimited for referral
    val createdAt: Long
)

@Serializable
enum class QrTokenKind {
    ONBOARDING,   // Driver onboarding QR
    REFERRAL     // Client referral QR
}

/**
 * Referral - tracks client referrals.
 * Per PLAN.md §19
 */
@Serializable
data class Referral(
    val id: String,
    val referrerUserId: String,
    val referredUserId: String?,
    val referralCode: String?,
    val bonusAmountCents: Long,
    val status: ReferralStatus,
    val claimedAt: Long? = null,
    val createdAt: Long
)

@Serializable
enum class ReferralStatus {
    PENDING,
    CLAIMED,
    BONUS_APPLIED,
    EXPIRED
}

/**
 * Feature flag - runtime configuration.
 * Per PLAN.md §8.11
 */
@Serializable
data class FeatureFlag(
    val key: String,
    val value: String, // "true"/"false" for boolean, or numeric
    val description: String?,
    val updatedAt: Long
)

/**
 * Audit log entry.
 * Per PLAN.md §21.1
 */
@Serializable
data class AuditLogEntry(
    val id: String,
    val action: String,
    val actorType: ActorType,
    val actorId: String,
    val targetType: String?,
    val targetId: String?,
    val metadata: String?, // JSON string
    val createdAt: Long
)

@Serializable
enum class ActorType {
    CLIENT,
    DRIVER,
    ADMIN,
    SYSTEM,
    DISPATCH
}

/**
 * Notification.
 * Per PLAN.md §20 - FCM and in-app
 */
@Serializable
data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: String?, // JSON payload
    val isRead: Boolean = false,
    val createdAt: Long
)

@Serializable
enum class NotificationType {
    JOB_OFFER,
    JOB_ACCEPTED,
    JOB_STARTED,
    JOB_COMPLETED,
    JOB_CANCELLED,
    KYC_APPROVED,
    KYC_REJECTED,
    PAYOUT_APPROVED,
    PAYOUT_REJECTED,
    REFERRAL_BONUS,
    SYSTEM
}
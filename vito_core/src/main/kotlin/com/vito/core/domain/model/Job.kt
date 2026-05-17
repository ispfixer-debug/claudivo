package com.vito.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Job types in Vito platform.
 * Per PLAN.md §12 - RIDE, SEND, MART types
 */
@Serializable
enum class JobType {
    RIDE,    // Passenger transport
    SEND,    // Package delivery
    MART     // Convenience store delivery
}

/**
 * Job status lifecycle.
 * Per PLAN.md §12 - full state machine
 */
@Serializable
enum class JobStatus {
    // Client states
    REQUESTED,      // Created, searching for driver
    SEARCHING,     // Dispatch is looking
    
    // Accepted flow
    ACCEPTED,      // Driver accepted
    EN_ROUTE,      // Driver heading to pickup
    ARRIVED,       // Driver at pickup
    IN_PROGRESS,   // Trip started (RIDE) or picked up (SEND/MART)
    COMPLETED,      // Successfully completed
    
    // Cancellation
    CANCELLED,     // Cancelled (may incur fee)
}

/**
 * Job - core ride/delivery/delivery entity.
 * Per PLAN.md §12.2
 */
@Serializable
data class Job(
    val id: String,
    val clientId: String,
    val driverId: String?,
    val type: JobType,
    val status: JobStatus,
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val destinationAddress: String?,
    val destinationLat: Double?,
    val destinationLng: Double?,
    val fareCents: Long,
    val distanceM: Int,
    val estimatedDurationM: Int,
    val rating: Int? = null,
    val ratingComment: String? = null,
    val cancellationFeeCents: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long? = null
)

/**
 * Dispatch offer - sent to drivers.
 * Per PLAN.md §13.3
 */
@Serializable
data class DispatchOffer(
    val id: String,
    val jobId: String,
    val driverId: String,
    val status: DispatchOfferStatus,
    val expiresAt: Long,
    val fareCents: Long,
    val distanceFromDriverM: Int,
    val createdAt: Long
)

@Serializable
enum class DispatchOfferStatus {
    PENDING,   // Sent, waiting for response
    ACCEPTED,  // Driver accepted
    DECLINED,  // Driver declined
    EXPIRED,   // Timer ran out
    CANCELLED  // Job cancelled
}

/**
 * Fare calculation.
 * Per PLAN.md §8.13
 */
@Serializable
data class FareEstimate(
    val estimatedFareCents: Long,
    val distanceM: Int,
    val estimatedDurationM: Int,
    val fareBreakdown: FareBreakdown? = null
)

@Serializable
data class FareBreakdown(
    val baseFareCents: Long,
    val distanceFareCents: Long,
    val timeFareCents: Long,
    val platformFeeCents: Long,
    val tipSuggestionCents: List<Long> = listOf(0, 100, 200, 500)
)

/**
 * Driver earnings per job.
 */
@Serializable
data class JobEarnings(
    val jobId: String,
    val driverId: String,
    val grossFareCents: Long,
    val platformFeeCents: Long,
    val netEarningsCents: Long,
    val createdAt: Long
)
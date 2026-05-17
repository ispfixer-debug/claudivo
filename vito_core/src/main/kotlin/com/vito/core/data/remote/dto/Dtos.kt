package com.vito.core.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects for Supabase API.
 * Per PLAN.md - mirror database schema exactly
 */

// User DTOs
@Serializable
data class UserDto(
    val id: String,
    val phone: String,
    val display_name: String? = null,
    val role: String,
    val language: String = "en",
    val fcm_token: String? = null,
    val is_active: Boolean = true,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class ClientDto(
    val user_id: String,
    val email: String? = null,
    val rating: Float = 0f,
    val rating_count: Int = 0,
    val wallet_balance_cents: Long = 0,
    val referrer_code: String? = null,
    val referred_by_code: String? = null,
    val total_trips: Int = 0
)

@Serializable
data class DriverDto(
    val user_id: String,
    val rating: Float = 0f,
    val rating_count: Int = 0,
    val wallet_balance_cents: Long = 0,
    val car_make: String = "",
    val car_model: String = "",
    val car_color: String = "",
    val car_plate: String = "",
    val car_year: Int = 0,
    val car_photo_url: String? = null,
    val kyc_status: String = "not_started",
    val stripe_account_id: String? = null,
    val stripe_onboarding_complete: Boolean = false,
    val is_online: Boolean = false,
    val driver_referral_code: String
)

// Job DTOs
@Serializable
data class JobDto(
    val id: String,
    val client_id: String,
    val driver_id: String? = null,
    val type: String,
    val status: String,
    val pickup_address: String,
    val pickup_lat: Double,
    val pickup_lng: Double,
    val destination_address: String? = null,
    val destination_lat: Double? = null,
    val destination_lng: Double? = null,
    val fare_cents: Long,
    val distance_m: Int = 0,
    val estimated_duration_m: Int = 0,
    val rating: Int? = null,
    val rating_comment: String? = null,
    val cancellation_fee_cents: Long? = null,
    val package_description: String? = null,
    val created_at: String,
    val updated_at: String,
    val completed_at: String? = null
)

@Serializable
data class DispatchOfferDto(
    val id: String,
    val job_id: String,
    val driver_id: String,
    val status: String,
    val expires_at: String,
    val fare_cents: Long,
    val distance_from_driver_m: Int = 0,
    val created_at: String
)

// Create requests
@Serializable
data class CreateRideRequest(
    val pickup_address: String,
    val pickup_lat: Double,
    val pickup_lng: Double,
    val destination_address: String,
    val destination_lat: Double,
    val destination_lng: Double
)

@Serializable
data class CreateSendRequest(
    val pickup_address: String,
    val pickup_lat: Double,
    val pickup_lng: Double,
    val destination_address: String,
    val destination_lat: Double,
    val destination_lng: Double,
    val package_description: String
)

// Wallet DTOs
@Serializable
data class WalletDto(
    val id: String,
    val user_id: String,
    val balance_cents: Long = 0,
    val stripe_customer_id: String? = null,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class TransactionDto(
    val id: String,
    val wallet_id: String,
    val type: String,
    val amount_cents: Long,
    val job_id: String? = null,
    val payout_request_id: String? = null,
    val stripe_payment_intent_id: String? = null,
    val description: String? = null,
    val created_at: String
)

@Serializable
data class TopUpRequest(
    val amount_cents: Long
)

@Serializable
data class PayoutRequestDto(
    val id: String,
    val driver_id: String,
    val amount_cents: Long,
    val status: String,
    val stripe_transfer_id: String? = null,
    val admin_note: String? = null,
    val created_at: String,
    val processed_at: String? = null
)

// QR Token DTOs
@Serializable
data class QrTokenDto(
    val id: String,
    val token: String,
    val kind: String,
    val created_by_user_id: String? = null,
    val target_user_id: String? = null,
    val is_active: Boolean = true,
    val expires_at: String,
    val redeemed_at: String? = null,
    val scan_count: Int = 0,
    val max_scans: Int = 1,
    val created_at: String
)

// KYC DTOs
@Serializable
data class KycDocumentDto(
    val id: String,
    val driver_id: String,
    val doc_type: String,
    val image_url: String? = null,
    val status: String = "pending",
    val rejection_reason: String? = null,
    val submitted_at: String? = null,
    val reviewed_at: String? = null,
    val created_at: String
)

@Serializable
data class KycSubmitRequest(
    val doc_type: String,
    val image_url: String
)

// Auth DTOs
@Serializable
data class OtpRequest(
    val phone: String
)

@Serializable
data class OtpVerifyRequest(
    val phone: String,
    val code: String
)

@Serializable  
data class RegisterClientRequest(
    val phone: String,
    val display_name: String,
    val referral_code: String? = null
)

@Serializable
data class RegisterDriverRequest(
    val phone: String,
    val display_name: String,
    val car_make: String,
    val car_model: String,
    val car_color: String,
    val car_plate: String,
    val car_year: Int
)

// Response types
@Serializable
data class AuthResponse(
    val user: UserDto,
    val access_token: String,
    val refresh_token: String
)

@Serializable
data class FareEstimateResponse(
    val estimated_fare_cents: Long,
    val distance_m: Int,
    val estimated_duration_m: Int,
    val base_fare_cents: Long,
    val distance_fare_cents: Long,
    val time_fare_cents: Long,
    val platform_fee_cents: Long,
    val tip_suggestions: List<Long> = listOf(0, 100, 200, 500)
)
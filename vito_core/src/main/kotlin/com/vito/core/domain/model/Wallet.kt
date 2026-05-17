package com.vito.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Wallet - payment and transactions.
 * Per PLAN.md §14 - Stripe Customer + Connect
 */
@Serializable
data class Wallet(
    val id: String,
    val userId: String,
    val balanceCents: Long,
    val stripeCustomerId: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Transaction types.
 */
@Serializable
enum class TransactionType {
    TOP_UP,
    RIDE_FARE,
    RIDE_FARE_REFUND,
    DELIVERY_FARE,
    DELIVERY_FARE_REFUND,
    MART_ORDER,
    MART_ORDER_REFUND,
    CANCELLATION_FEE,
    PAYOUT,
    BONUS,
    ADJUSTMENT
}

/**
 * Transaction record.
 */
@Serializable
data class Transaction(
    val id: String,
    val walletId: String,
    val type: TransactionType,
    val amountCents: Long,
    val jobId: String? = null,
    val payoutRequestId: String? = null,
    val stripePaymentIntentId: String? = null,
    val description: String? = null,
    val createdAt: Long
)

/**
 * Payout request - driver requests to withdraw.
 * Per PLAN.md §15.3
 */
@Serializable
data class PayoutRequest(
    val id: String,
    val driverId: String,
    val amountCents: Long,
    val status: PayoutStatus,
    val stripeTransferId: String? = null,
    val adminNote: String? = null,
    val createdAt: Long,
    val processedAt: Long? = null
)

@Serializable
enum class PayoutStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PAID,
    FAILED
}
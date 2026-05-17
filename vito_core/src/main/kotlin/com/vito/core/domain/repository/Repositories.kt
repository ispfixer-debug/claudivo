package com.vito.core.domain.repository

import com.vito.core.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Auth repository interface.
 * Per PLAN.md §10 - phone OTP + PIN auth
 */
interface AuthRepository {
    val currentUser: Flow<User?>
    val isAuthenticated: Flow<Boolean>
    
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, code: String): Result<User>
    suspend fun loginWithPassword(phone: String, pin: String): Result<User>
    suspend fun registerClient(phone: String, displayName: String, referralCode: String?): Result<User>
    suspend fun registerDriver(phone: String, displayName: String): Result<User>
    suspend fun changePin(currentPin: String, newPin: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun logout()
}

/**
 * User repository interface.
 */
interface UserRepository {
    fun getClientProfile(clientId: String): Flow<ClientProfile?>
    fun getDriverProfile(driverId: String): Flow<DriverProfile?>
    
    suspend fun updateClientProfile(profile: ClientProfile): Result<ClientProfile>
    suspend fun updateDriverProfile(profile: DriverProfile): Result<DriverProfile>
    suspend fun updateFcmToken(userId: String, token: String): Result<Unit>
}

/**
 * Job repository interface.
 * Per PLAN.md §12
 */
interface JobRepository {
    fun getJobsForClient(clientId: String): Flow<List<Job>>
    fun getJobsForDriver(driverId: String): Flow<List<Job>>
    fun getJobById(jobId: String): Flow<Job?>
    fun getActiveJobForClient(clientId: String): Flow<Job?>
    fun getActiveJobForDriver(driverId: String): Flow<Job?>
    
    suspend fun requestRide(
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        destinationAddress: String,
        destinationLat: Double,
        destinationLng: Double
    ): Result<Job>
    
    suspend fun requestSend(
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        destinationAddress: String,
        destinationLat: Double,
        destinationLng: Double,
        packageDescription: String
    ): Result<Job>
    
    suspend fun cancelJob(jobId: String): Result<Job>
    suspend fun rateJob(jobId: String, rating: Int, comment: String?): Result<Unit>
}

/**
 * Dispatch offer repository interface.
 */
interface DispatchRepository {
    fun getPendingOffersForDriver(driverId: String): Flow<List<DispatchOffer>>
    
    suspend fun acceptOffer(offerId: String): Result<Job>
    suspend fun declineOffer(offerId: String): Result<Unit>
    suspend fun updateJobStatus(jobId: String, status: JobStatus): Result<Job>
    suspend fun completeJob(jobId: String): Result<Job>
}

/**
 * Wallet repository interface.
 */
interface WalletRepository {
    fun getWallet(userId: String): Flow<Wallet?>
    fun getTransactions(walletId: String): Flow<List<Transaction>>
    
    suspend fun topUp(walletId: String, amountCents: Long, stripePaymentIntentId: String): Result<Transaction>
    suspend fun requestPayout(driverId: String, amountCents: Long): Result<PayoutRequest>
}

/**
 * Location repository interface.
 */
interface LocationRepository {
    fun getDriverLocation(driverId: String): Flow<Location?>
    fun getNearbyDrivers(lat: Double, lng: Double, radiusM: Int): Flow<List<Location>>
    
    suspend fun updateDriverLocation(driverId: String, lat: Double, lng: Double): Result<Unit>
}

data class Location(
    val driverId: String,
    val lat: Double,
    val lng: Double,
    val bearing: Float? = null,
    val isOnline: Boolean = true,
    val updatedAt: Long
)

/**
 * QR Token repository interface.
 */
interface QrTokenRepository {
    suspend fun generateOnboardingQr(): Result<QrToken>
    suspend fun generateReferralQr(driverId: String): Result<QrToken>
    suspend fun validateToken(token: String): Result<QrToken>
    suspend fun redeemToken(token: String, userId: String): Result<Referral>
    suspend fun revokeToken(tokenId: String): Result<Unit>
}

/**
 * Feature flags repository.
 */
interface FeatureFlagRepository {
    fun getFeatureFlags(): Flow<List<FeatureFlag>>
    suspend fun updateFeatureFlag(key: String, value: String): Result<FeatureFlag>
}
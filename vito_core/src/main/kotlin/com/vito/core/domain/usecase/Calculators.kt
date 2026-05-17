package com.vito.core.domain.usecase

import com.vito.core.domain.model.*
import com.vito.core.domain.repository.JobRepository
import com.vito.core.domain.repository.WalletRepository

/**
 * Fare Calculator - business logic for fare estimation.
 * Per PLAN.md §8.13
 */
class FareCalculator {

    data class FareConfig(
        val baseFareCents: Long = 250,
        val perKmCents: Long = 100,
        val perMinCents: Long = 25,
        val platformFeePercent: Int = 15,
        val cancellationFeeCents: Long = 200
    )

    fun calculate(
        distanceMeters: Int,
        durationMinutes: Int,
        config: FareConfig = FareConfig()
    ): Long {
        val distanceKm = (distanceMeters / 1000.0).coerceAtLeast(1.0)
        val baseFare = config.baseFareCents
        val distanceFare = (distanceKm * config.perKmCents).toLong()
        val timeFare = (durationMinutes * config.perMinCents).toLong()
        
        val subtotal = baseFare + distanceFare + timeFare
        val platformFee = (subtotal * config.platformFeePercent / 100)
        
        return subtotal + platformFee
    }

    fun calculateWithTip(
        distanceMeters: Int,
        durationMinutes: Int,
        tipCents: Long,
        config: FareConfig = FareConfig()
    ): Long {
        return calculate(distanceMeters, durationMinutes, config) + tipCents
    }

    fun getTipSuggestions(config: FareConfig = FareConfig()) =
        listOf(0L, 100L, 200L, 500L)

    fun calculateCancellationFee(
        status: JobStatus,
        timeElapsedSeconds: Long = 0,
        config: FareConfig = FareConfig()
    ): Long {
        return when {
            status == JobStatus.ACCEPTED && timeElapsedSeconds < 120 -> 0 // Free within 2 min
            status == JobStatus.EN_ROUTE -> 0 // Can't cancel once en route
            else -> config.cancellationFeeCents
        }
    }
}

/**
 * Distance Calculator - Haversine formula for lat/lng.
 */
class DistanceCalculator {

    fun calculateHaversineDistance(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Int {
        val r = 6371000 // Earth radius in meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)
        
        val c = 2 * kotlin.math.atan2(
            kotlin.math.sqrt(a),
            kotlin.math.sqrt(1 - a)
        )
        
        return (r * c).toInt()
    }

    fun estimateDuration(distanceMeters: Int, avgSpeedKmh: Int = 30): Int {
        val distanceKm = distanceMeters / 1000.0
        return ((distanceKm / avgSpeedKmh) * 60).toInt()
    }
}

/**
 * Phone normalizer - E.164 format.
 */
object PhoneNormalizer {

    private val e164Regex = Regex("^\\+[1-9]\\d{1,14}$")

    fun normalize(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        
        return when {
            digits.length == 10 -> "+1$digits"
            digits.length == 11 && digits.startsWith("1") -> "+$digits"
            phone.startsWith("+") -> phone
            else -> "+$digits"
        }
    }

    fun isValid(phone: String): Boolean {
        val normalized = normalize(phone)
        return e164Regex.matches(normalized)
    }
}

/**
 * Use case: Calculate fare estimate.
 */
class CalculateFareUseCase(
    private val fareCalculator: FareCalculator = FareCalculator(),
    private val distanceCalculator: DistanceCalculator = DistanceCalculator()
) {
    operator fun invoke(
        pickupLat: Double, pickupLng: Double,
        destinationLat: Double, destinationLng: Double,
        config: FareCalculator.FareConfig = FareCalculator.FareConfig()
    ): FareEstimate {
        val distance = distanceCalculator.calculateHaversineDistance(
            pickupLat, pickupLng, destinationLat, destinationLng
        )
        val duration = distanceCalculator.estimateDuration(distance)
        val fare = fareCalculator.calculate(distance, duration, config)
        
        return FareEstimate(
            estimatedFareCents = fare,
            distanceM = distance,
            estimatedDurationM = duration,
            fareBreakdown = FareBreakdown(
                baseFareCents = config.baseFareCents,
                distanceFareCents = ((distance / 1000.0) * config.perKmCents).toLong(),
                timeFareCents = (duration * config.perMinCents).toLong(),
                platformFeeCents = (fare * config.platformFeePercent / 100),
                tipSuggestionCents = fareCalculator.getTipSuggestions(config)
            )
        )
    }
}
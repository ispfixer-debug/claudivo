// Fare estimation edge function
// PLAN §8.13 - fare formula: base + (distance * rate) + (duration * time_rate) + platform_fee

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const supabaseUrl = Deno.env.get('SUPABASE_URL')!
    const supabaseKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
    const { createClient } = await import('https://esm.sh/@supabase/supabase-js@2')
    const supabase = createClient(supabaseUrl, supabaseKey)

    // Get feature flags for fare calculation
    const { data: flags } = await supabase
      .from('vito_feature_flags')
      .select('key, value')
      .in('key', ['base_fare_cents', 'per_km_cents', 'per_min_cents', 'platform_fee_percent'])

    const getFlag = (key: string, def: number) => {
      const f = flags?.find(f => f.key === key)
      return f ? Number(f.value) : def
    }

    const baseFare = getFlag('base_fare_cents', 250)
    const perKm = getFlag('per_km_cents', 100)
    const perMin = getFlag('per_min_cents', 25)
    const platformFeePercent = getFlag('platform_fee_percent', 15)

    // Parse request
    const { pickup_lat, pickup_lng, destination_lat, destination_lng } = await req.json()

    if (!pickup_lat || !destination_lat) {
      return new Response(JSON.stringify({ error: 'Missing coordinates' }), {
        status: 400,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      })
    }

    // Calculate distance using Haversine formula
    const distanceM = calculateHaversineDistance(
      pickup_lat, pickup_lng,
      destination_lat, destination_lng
    )
    const distanceKm = Math.round(distanceM / 1000)

    // Estimate duration (assume 30 km/h average speed in city)
    const estimatedDurationM = Math.max(5, Math.round(distanceKm / 0.5))

    // Calculate fare
    const distanceFare = distanceKm * perKm
    const timeFare = estimatedDurationM * perMin
    const subtotal = baseFare + distanceFare + timeFare
    const platformFee = Math.round(subtotal * (platformFeePercent / 100))
    const totalFare = subtotal + platformFee

    return new Response(JSON.stringify({
      estimated_fare_cents: totalFare,
      distance_m: distanceM,
      estimated_duration_m: estimatedDurationM,
      base_fare_cents: baseFare,
      distance_fare_cents: distanceFare,
      time_fare_cents: timeFare,
      platform_fee_cents: platformFee,
      tip_suggestions: [0, 100, 200, 500]
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500,
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    })
  }
})

function toRad(deg: number) {
  return deg * Math.PI / 180
}

function calculateHaversineDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371000 // Earth radius in meters
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  const a = Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) ** 2
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return Math.round(R * c)
}
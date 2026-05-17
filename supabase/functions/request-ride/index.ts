// Request ride edge function
// PLAN §12 - creates a new ride job

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

    // Parse request
    const body = await req.json()
    const { 
      client_id, 
      pickup_address, pickup_lat, pickup_lng,
      destination_address, destination_lat, destination_lng
    } = body

    // Validate required fields
    if (!client_id || !pickup_address || !pickup_lat || !destination_address) {
      return new Response(JSON.stringify({ 
        error: 'Missing required fields' 
      }), { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } })
    }

    // Get fare estimate
    const { data: flags } = await supabase
      .from('vito_feature_flags')
      .select('value')
      .in('key', ['base_fare_cents', 'per_km_cents', 'per_min_cents', 'platform_fee_percent'])
      .then(r => r.data || [])

    const getFlag = (key: string, def: number) => {
      const f = flags.find(f => f.key === key)
      return f ? parseInt(f.value) : def
    }

    // Calculate distance
    const distanceM = Math.round(calculateDistance(
      pickup_lat, pickup_lng, destination_lat, destination_lng
    ))
    const distanceKm = Math.max(1, Math.round(distanceM / 1000))
    const durationM = Math.max(5, Math.round(distanceKm / 0.5)) // 30 km/h avg

    // Calculate fare
    const baseFare = getFlag('base_fare_cents', 250)
    const perKm = getFlag('per_km_cents', 100)
    const perMin = getFlag('per_min_cents', 25)
    const platformPercent = getFlag('platform_fee_percent', 15)
    
    const distanceFare = distanceKm * perKm
    const timeFare = durationM * perMin
    const subtotal = baseFare + distanceFare + timeFare
    const platformFee = Math.round(subtotal * platformPercent / 100)
    const fareCents = subtotal + platformFee

    // Create job
    const { data: job, error: jobError } = await supabase
      .from('vito_jobs')
      .insert({
        client_id,
        type: 'ride',
        status: 'searching',
        pickup_address,
        pickup_lat,
        pickup_lng,
        destination_address,
        destination_lat,
        destination_lng,
        fare_cents: fareCents,
        distance_m: distanceM,
        estimated_duration_m: durationM
      })
      .select()
      .single()

    if (jobError) throw jobError

    // TODO: Trigger dispatch algorithm
    // For now, just return the created job

    return new Response(JSON.stringify({ 
      data: job,
      estimated_fare_cents: fareCents,
      distance_m: distanceM,
      estimated_duration_m: durationM
    }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' } })

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500,
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    })
  }

  function toRad(deg: number) { return deg * Math.PI / 180 }

  function calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371000
    const dLat = toRad(lat2 - lat1)
    const dLon = toRad(lon2 - lon1)
    const a = Math.sin(dLat/2)**2 + Math.cos(toRad(lat1))*Math.cos(toRad(lat2))*Math.sin(dLon/2)**2
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    return R * c
  }
})

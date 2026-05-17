// Complete Job - Mark job as completed
// PLAN §10.6

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, driver_id, final_fare, distance_km, duration_min, signature } = await req.json()

    if (!job_id || !driver_id) {
      return new Response(JSON.stringify({ error: 'job_id and driver_id required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Calculate final fare per PLAN §8.13
    const base_fare = 25 // $2.50 base
    const per_km = 8 // $0.80/km  
    const per_min = 2 // $0.20/min
    
    const calculated_fare = final_fare || (base_fare + (distance_km || 0) * per_km + (duration_min || 0) * per_min)

    // In production: save to database, update wallet balances
    return new Response(JSON.stringify({
      success: true,
      job_id,
      status: 'completed',
      final_fare: calculated_fare,
      distance_km: distance_km || 0,
      duration_min: duration_min || 0,
      completed_at: new Date().toISOString()
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
// Update Job Status - Update status during ride/delivery
// PLAN §10.5

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

// Job status flow: pending -> accepted -> en_route -> arrived -> in_progress -> completed -> rated
const VALID_STATUSES = ['accepted', 'en_route', 'arrived', 'in_progress', 'completed', 'cancelled']

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, status, driver_location } = await req.json()

    if (!job_id || !status) {
      return new Response(JSON.stringify({ error: 'job_id and status required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    if (!VALID_STATUSES.includes(status)) {
      return new Response(JSON.stringify({ error: 'Invalid status' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    return new Response(JSON.stringify({
      success: true,
      job_id,
      status,
      driver_location: driver_location || null,
      updated_at: new Date().toISOString()
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
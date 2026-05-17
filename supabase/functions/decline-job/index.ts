// Decline Job - Driver declines a dispatch offer
// PLAN §10.3

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, driver_id, reason } = await req.json()

    if (!job_id || !driver_id) {
      return new Response(JSON.stringify({ error: 'job_id and driver_id required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Log decline and return
    console.log(`Job declined: ${job_id} by driver ${driver_id}, reason: ${reason}`)

    return new Response(JSON.stringify({
      success: true,
      job_id,
      status: 'declined',
      reason: reason || null
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
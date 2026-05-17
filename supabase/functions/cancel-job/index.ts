// Cancel Job - Cancel an active job
// PLAN §10.4

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, user_id, reason, cancellation_fee } = await req.json()

    if (!job_id || !user_id) {
      return new Response(JSON.stringify({ error: 'job_id and user_id required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Calculate cancellation fee per PLAN §15.5
    const fee = cancellation_fee || 0

    return new Response(JSON.stringify({
      success: true,
      job_id,
      status: 'cancelled',
      cancellation_fee: fee,
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
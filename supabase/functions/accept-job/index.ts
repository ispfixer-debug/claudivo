// Accept Job - Driver accepts a dispatch offer
// PLAN §10.2

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, driver_id } = await req.json()

    if (!job_id || !driver_id) {
      return new Response(JSON.stringify({ error: 'job_id and driver_id required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // In production: query database for job status
    // Return job accepted response
    return new Response(JSON.stringify({
      success: true,
      job_id,
      driver_id,
      status: 'accepted'
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
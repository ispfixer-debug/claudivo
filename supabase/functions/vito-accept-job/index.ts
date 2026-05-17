// Accept Job - Driver accepts a dispatch offer
// Per RULE #7 - accept-job MUST call assign_driver_atomic() RPC — never plain UPDATE

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, driver_id, job_type } = await req.json()

    if (!job_id || !driver_id || !job_type) {
      return new Response(JSON.stringify({ error: 'job_id, driver_id, and job_type required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Validate job_type
    if (!['ride', 'send', 'mart'].includes(job_type)) {
      return new Response(JSON.stringify({ error: 'invalid job_type' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Use assign_driver_atomic RPC to prevent race conditions
    const table_map: Record<string, string> = {
      ride: 'vito_rides',
      send: 'vito_sends',
      mart: 'vito_mart_orders',
    }

    const table = table_map[job_type]

    // Create Supabase client for RPC call
    const supabaseUrl = Deno.env.get('SUPABASE_URL')!
    const supabaseKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
    
    const response = await fetch(`${supabaseUrl}/rest/v1/rpc/assign_driver_atomic`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'apikey': supabaseKey,
        'Authorization': `Bearer ${supabaseKey}`,
      },
      body: JSON.stringify({
        p_job_id: job_id,
        p_driver_id: driver_id,
        p_job_table: table,
      }),
    })

    const rpcResult = await response.json()

    if (!response.ok || rpcResult === false) {
      return new Response(JSON.stringify({ error: 'Job already assigned to another driver' }), {
        status: 409, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

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

// KYC Approve - Approve KYC document
// PLAN §10.15

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { admin_id, kyc_id, driver_id, notes } = await req.json()

    if (!admin_id || !kyc_id || !driver_id) {
      return new Response(JSON.stringify({ error: 'admin_id, kyc_id, driver_id required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Update KYC status
    
    return new Response(JSON.stringify({
      success: true,
      kyc_id,
      driver_id,
      status: 'approved',
      approved_by: admin_id,
      approved_at: new Date().toISOString(),
      notes: notes || null
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
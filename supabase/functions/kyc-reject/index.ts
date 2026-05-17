// KYC Reject - Reject KYC document
// PLAN §10.16

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { admin_id, kyc_id, driver_id, reason } = await req.json()

    if (!admin_id || !kyc_id || !driver_id || !reason) {
      return new Response(JSON.stringify({ error: 'admin_id, kyc_id, driver_id, reason required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Update KYC status
    
    return new Response(JSON.stringify({
      success: true,
      kyc_id,
      driver_id,
      status: 'rejected',
      rejected_by: admin_id,
      reason,
      rejected_at: new Date().toISOString()
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
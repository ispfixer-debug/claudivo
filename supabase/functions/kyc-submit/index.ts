// KYC Submit - Submit KYC documents
// PLAN §10.14

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { driver_id, document_type, front_image, back_image, selfie_image } = await req.json()

    if (!driver_id || !document_type || !front_image) {
      return new Response(JSON.stringify({ error: 'driver_id, document_type, front_image required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // In production: upload to storage bucket, create KYC record
    const kyc_id = crypto.randomUUID()

    return new Response(JSON.stringify({
      success: true,
      kyc_id,
      driver_id,
      document_type,
      status: 'pending_review',
      submitted_at: new Date().toISOString()
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
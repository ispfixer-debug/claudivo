// Validate QR Token - Validate QR token for referral/referral
// PLAN §10.17

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { token } = await req.json()

    if (!token) {
      return new Response(JSON.stringify({ error: 'token required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Lookup token in database
    // Determine type: onboarding (single-use) vs referral (multi-use)
    
    const is_onboarding = token.startsWith('ONBOARD_')
    const is_referral = token.startsWith('REF_')
    
    const token_type = is_onboarding ? 'onboarding' : is_referral ? 'referral' : 'unknown'
    const max_uses = is_referral ? 5 : 1 // referral can be used multiple times

    return new Response(JSON.stringify({
      valid: true,
      token,
      type: token_type,
      max_uses,
      uses_remaining: max_uses // In production: query actual count
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
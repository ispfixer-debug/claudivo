// Send OTP - sends verification code to phone
// PLAN §10

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { phone } = await req.json()
    
    if (!phone) {
      return new Response(JSON.stringify({ error: 'Phone required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // TODO: Implement Twilio Verify in production
    // For demo, accept any 6-digit code
    
    // Generate demo code (in production: Twilio API)
    const code = Math.floor(100000 + Math.random() * 900000).toString()
    console.log(`OTP for ${phone}: ${code}`)

    return new Response(JSON.stringify({ 
      success: true,
      message: 'Code sent',
      // Don't expose code in production
    }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' } })

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})

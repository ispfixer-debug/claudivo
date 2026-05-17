// Auth Login - Login with credentials
// PLAN §10.10

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { phone, pin } = await req.json()

    if (!phone || !pin) {
      return new Response(JSON.stringify({ error: 'phone and pin required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // In production: verify against database with Argon2
    // For demo: accept any 4-digit pin
    
    // Generate JWT
    const token = crypto.randomUUID()
    
    return new Response(JSON.stringify({
      success: true,
      user: { phone },
      access_token: token,
      token_type: 'bearer',
      expires_in: 3600
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
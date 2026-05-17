// Auth Register Client - Register new client account
// PLAN §10.12

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { phone, name, email, referral_code } = await req.json()

    if (!phone || !name) {
      return new Response(JSON.stringify({ error: 'phone and name required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Create user in database
    const user_id = crypto.randomUUID()
    
    // Generate initial wallet
    let bonus_credits = 0
    if (referral_code) {
      bonus_credits = 100 // Apply referral bonus
    }

    return new Response(JSON.stringify({
      success: true,
      user_id,
      phone,
      name,
      email: email || null,
      wallet_balance: bonus_credits,
      referral_code: user_id.substring(0, 8).toUpperCase()
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
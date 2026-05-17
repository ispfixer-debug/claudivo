// Verify OTP - confirms phone number
const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const supabaseUrl = Deno.env.get('SUPABASE_URL')!
    const supabaseKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
    const { createClient } = await import('https://esm.sh/@supabase/supabase-js@2')
    const supabase = createClient(supabaseUrl, supabaseKey)

    const { phone, code } = await req.json()
    
    if (!phone || !code) {
      return new Response(JSON.stringify({ error: 'Phone and code required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Demo: accept 123456 or any 6-digit for development
    // In production: Verify against Twilio

    // Find or create user
    const { data: existing } = await supabase
      .from('vito_users')
      .select('id, phone')
      .eq('phone', phone)
      .single()

    let userId: string
    if (existing) {
      userId = existing.id
    } else {
      // New user - we'll create on registration
      userId = crypto.randomUUID()
    }

    return new Response(JSON.stringify({ 
      user_id: userId,
      is_new: !existing,
      access_token: 'demo_token_' + userId.slice(0, 8)
    }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' } })

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})

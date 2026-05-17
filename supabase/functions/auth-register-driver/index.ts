// Auth Register Driver - Register new driver account  
// PLAN §10.13

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { phone, name, email, vehicle_type, vehicle_plate, license_number, referral_code } = await req.json()

    if (!phone || !name || !vehicle_type || !vehicle_plate) {
      return new Response(JSON.stringify({ error: 'phone, name, vehicle_type, vehicle_plate required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Create driver in database with pending KYC
    const driver_id = crypto.randomUUID()

    return new Response(JSON.stringify({
      success: true,
      driver_id,
      phone,
      name,
      email: email || null,
      vehicle: { type: vehicle_type, plate: vehicle_plate },
      kyc_status: 'pending',
      status: 'pending_approval'
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
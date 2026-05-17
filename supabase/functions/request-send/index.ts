// Request Send - Package delivery request
// PLAN §10.8

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { sender_id, sender_name, sender_phone, pickup_address, pickup_notes, recipient_name, recipient_phone, delivery_address, delivery_notes, package_type, package_weight, package_value, payment_method_id } = await req.json()

    if (!sender_id || !pickup_address || !delivery_address || !recipient_name || !recipient_phone) {
      return new Response(JSON.stringify({ error: 'Missing required fields' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Calculate fare based on distance/weight
    const base_fare = 30 // $3.00 base
    const weight_multiplier = package_weight > 5 ? (package_weight / 5) * 0.5 : 0 // $0.50 per 5kg over 5kg
    
    const job_id = crypto.randomUUID()
    
    return new Response(JSON.stringify({
      success: true,
      job_id,
      order: {
        type: 'send',
        sender: { id: sender_id, name: sender_name, phone: sender_phone },
        recipient: { name: recipient_name, phone: recipient_phone },
        pickup: { address: pickup_address, notes: pickup_notes },
        delivery: { address: delivery_address, notes: delivery_notes },
        package: { type: package_type, weight: package_weight, value: package_value },
        fare: base_fare + weight_multiplier,
        payment_method_id
      },
      status: 'pending_dispatch'
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
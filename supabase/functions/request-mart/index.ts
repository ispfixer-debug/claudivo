// Request Mart - Convenience store delivery request
// PLAN §10.7

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { user_id, pickup_address, delivery_address, items, payment_method_id } = await req.json()

    if (!user_id || !pickup_address || !delivery_address || !items) {
      return new Response(JSON.stringify({ error: 'Missing required fields' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Calculate order total
    const subtotal = items.reduce((sum: number, item: any) => sum + (item.price * item.quantity), 0)
    const delivery_fee = 15 // $1.50
    const service_fee = Math.round(subtotal * 0.05) // 5%
    
    const job_id = crypto.randomUUID()
    
    return new Response(JSON.stringify({
      success: true,
      job_id,
      order: {
        user_id,
        pickup_address,
        delivery_address,
        items,
        subtotal,
        delivery_fee,
        service_fee,
        total: subtotal + delivery_fee + service_fee,
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
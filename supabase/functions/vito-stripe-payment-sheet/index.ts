// vito-stripe-payment-sheet - Create Stripe payment sheet
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { user_id, amount, type } = await req.json()
    if (!user_id || !amount || !type) return new Response(JSON.stringify({ error: "user_id, amount, and type required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    return new Response(JSON.stringify({ client_secret: "pi_placeholder", publishable_key: "pk_placeholder" }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

// vito-qr-gen - Generate QR invitation token
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { user_type, driver_id } = await req.json()
    if (!user_type) return new Response(JSON.stringify({ error: "user_type required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    if (!["client_referral", "driver_onboard"].includes(user_type)) return new Response(JSON.stringify({ error: "invalid user_type" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    const token = crypto.randomUUID().split("-")[0].toUpperCase()
    const expires = new Date()
    expires.setDate(expires.getDate() + 30)
    const record = { token, type: user_type, driver_id: driver_id || null, valid: true, expires_at: expires.toISOString(), created_at: new Date().toISOString() }
    const resp = await fetch(supabaseUrl + "/rest/v1/vito_qr_tokens", { method: "POST", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify(record) })
    const newToken = await resp.json()
    return new Response(JSON.stringify({ token: newToken.token, type: newToken.type, expires_at: newToken.expires_at }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

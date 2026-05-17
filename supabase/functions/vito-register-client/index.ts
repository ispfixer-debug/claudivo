// vito-register-client - Client registration with invitation token
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { username, display_name, pin, invitation_token } = await req.json()
    if (!username || !pin || !invitation_token) return new Response(JSON.stringify({ error: "username, pin, and invitation_token required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    if (!/^[a-z0-9_]{3,20}$/.test(username)) return new Response(JSON.stringify({ error: "Invalid username format" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    if (!/^\d{6}$/.test(pin)) return new Response(JSON.stringify({ error: "PIN must be 6 digits" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    // Validate invitation token
    const tokenResp = await fetch(supabaseUrl + "/rest/v1/vito_qr_tokens?token=eq." + invitation_token + "&type=eq.client_referral&valid=eq.true&select=id,driver_id", { headers: { apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey } })
    const tokens = await tokenResp.json()
    if (!tokens || tokens.length === 0) return new Response(JSON.stringify({ error: "Invalid or expired invitation token" }), { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const token = tokens[0]
    // Check username not taken
    const userResp = await fetch(supabaseUrl + "/rest/v1/vito_users?username=eq." + encodeURIComponent(username) + "&select=id", { headers: { apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey } })
    const existing = await userResp.json()
    if (existing && existing.length > 0) return new Response(JSON.stringify({ error: "Username already taken" }), { status: 409, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    // Create user
    const user = { username, display_name: display_name || username, pin_hash: pin, referral_driver_id: token.driver_id, created_at: new Date().toISOString() }
    const createResp = await fetch(supabaseUrl + "/rest/v1/vito_users", { method: "POST", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify(user) })
    if (!createResp.ok) { const err = await createResp.text(); return new Response(JSON.stringify({ error: err }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
    const newUser = await createResp.json()
    // Mark token as used
    await fetch(supabaseUrl + "/rest/v1/vito_qr_tokens?id=eq." + token.id, { method: "PATCH", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify({ used_by: newUser.id, used_at: new Date().toISOString() }) })
    // Create wallet
    await fetch(supabaseUrl + "/rest/v1/vito_wallets", { method: "POST", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify({ user_id: newUser.id, balance: 0 }) })
    return new Response(JSON.stringify({ success: true, user_id: newUser.id, username }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

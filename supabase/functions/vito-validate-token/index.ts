// vito-validate-token - Validate QR invitation token
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { token } = await req.json()
    if (!token) return new Response(JSON.stringify({ error: "token required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    const resp = await fetch(supabaseUrl + "/rest/v1/vito_qr_tokens?token=eq." + token + "&valid=eq.true&select=id,type,driver_id,expires_at", { headers: { apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey } })
    const tokens = await resp.json()
    if (!tokens || tokens.length === 0) return new Response(JSON.stringify({ valid: false, error: "Invalid or expired token" }), { status: 404, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const t = tokens[0]
    if (t.expires_at && new Date(t.expires_at) < new Date()) return new Response(JSON.stringify({ valid: false, error: "Token expired" }), { status: 404, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    return new Response(JSON.stringify({ valid: true, type: t.type, driver_id: t.driver_id }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

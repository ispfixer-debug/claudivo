// vito-suspend-user - Admin suspend user
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { target_id, target_type, suspend } = await req.json()
    if (!target_id || !target_type || suspend === undefined) return new Response(JSON.stringify({ error: "target_id, target_type, and suspend required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const table = target_type === "client" ? "vito_users" : target_type === "driver" ? "vito_drivers" : null
    if (!table) return new Response(JSON.stringify({ error: "invalid target_type" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    await fetch(supabaseUrl + "/rest/v1/" + table + "?id=eq." + target_id, { method: "PATCH", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify({ is_suspended: suspend }) })
    return new Response(JSON.stringify({ success: true }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

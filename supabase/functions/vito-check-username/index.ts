// vito-check-username - Check if username is available
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { username, user_type } = await req.json()
    if (!username || !user_type) return new Response(JSON.stringify({ error: "username and user_type required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    if (!["client", "driver"].includes(user_type)) return new Response(JSON.stringify({ error: "invalid user_type" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const table = user_type === "client" ? "vito_users" : "vito_drivers"
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    const resp = await fetch(supabaseUrl + "/rest/v1/" + table + "?username=eq." + encodeURIComponent(username) + "&select=id", { headers: { apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey } })
    const users = await resp.json()
    return new Response(JSON.stringify({ available: !users || users.length === 0 }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

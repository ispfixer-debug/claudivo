// vito-approve-car-photo - Admin approve driver car photo
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { driver_id, approved } = await req.json()
    if (!driver_id || approved === undefined) return new Response(JSON.stringify({ error: "driver_id and approved required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    await fetch(supabaseUrl + "/rest/v1/vito_drivers?id=eq." + driver_id, { method: "PATCH", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify({ car_photo_approved: approved }) })
    return new Response(JSON.stringify({ success: true }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

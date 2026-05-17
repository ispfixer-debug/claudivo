// vito-create-ride - Create a new ride request
const corsHeaders = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" }
Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response(null, { headers: corsHeaders })
  try {
    const { client_id, pickup_lat, pickup_lng, pickup_address, dest_lat, dest_lng, dest_address, distance_km, fare_min, fare_max } = await req.json()
    if (!client_id || !pickup_lat || !dest_lat) return new Response(JSON.stringify({ error: "client_id and locations required" }), { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } })
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    const ride = { client_id, pickup_lat, pickup_lng, pickup_address, dest_lat, dest_lng, dest_address, distance_km: distance_km || 0, fare_min: fare_min || 0, fare_max: fare_max || 0, status: "pending", created_at: new Date().toISOString() }
    const resp = await fetch(supabaseUrl + "/rest/v1/vito_rides", { method: "POST", headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey }, body: JSON.stringify(ride) })
    if (!resp.ok) { const err = await resp.text(); return new Response(JSON.stringify({ error: err }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
    const newRide = await resp.json()
    return new Response(JSON.stringify({ ride_id: newRide.id, status: newRide.status }), { headers: { ...corsHeaders, "Content-Type": "application/json" } })
  } catch (error) { return new Response(JSON.stringify({ error: error.message }), { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }) }
})

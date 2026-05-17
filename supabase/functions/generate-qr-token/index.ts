// $name function
const corsHeaders = { 'Access-Control-Allow-Origin': '*', 'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type' }
Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') return new Response(null, { headers: corsHeaders })
  try {
    const body = await req.json().catch(() => ({}))
    return new Response(JSON.stringify({ success: true, function: "$name", ...body }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' } })
  } catch (e) { return new Response(JSON.stringify({ error: e.message }), { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }) }
})

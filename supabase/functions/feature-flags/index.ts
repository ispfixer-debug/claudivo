// Feature flags endpoint
// PLAN §8.11

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const supabaseUrl = Deno.env.get('SUPABASE_URL')!
    const supabaseKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
    
    const { createClient } = await import('https://esm.sh/@supabase/supabase-js@2')
    const supabase = createClient(supabaseUrl, supabaseKey)

    const { data, error } = await supabase
      .from('vito_feature_flags')
      .select('key, value')
    
    if (error) {
      throw error
    }

    const flags: Record<string, any> = {}
    for (const row of data || []) {
      if (row.value === 'true') flags[row.key] = true
      else if (row.value === 'false') flags[row.key] = false
      else if (!isNaN(Number(row.value))) flags[row.key] = Number(row.value)
      else flags[row.key] = row.value
    }

    return new Response(JSON.stringify(flags), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500,
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    })
  }
})

// Rate Job - Rate completed job
// PLAN §10.9

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { job_id, user_id, rating, comment } = await req.json()

    if (!job_id || !user_id || !rating) {
      return new Response(JSON.stringify({ error: 'job_id, user_id and rating required' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    if (rating < 1 || rating > 5) {
      return new Response(JSON.stringify({ error: 'Rating must be 1-5' }), {
        status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      })
    }

    // Log rating (in production: update database)
    console.log(`Job ${job_id} rated ${rating} by ${user_id}`)

    return new Response(JSON.stringify({
      success: true,
      job_id,
      rating,
      comment: comment || null,
      rated_at: new Date().toISOString()
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' }
    })
  }
})
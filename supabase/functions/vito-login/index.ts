// vito-login - Authenticate user with username + PIN
// Per RULE #1 - username + 6-digit PIN ONLY. No phone, no OTP, no email.

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
}

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const { username, pin, user_type } = await req.json()

    if (!username || !pin || !user_type) {
      return new Response(JSON.stringify({ error: "username, pin, and user_type required" }), {
        status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    if (!["client", "driver", "admin"].includes(user_type)) {
      return new Response(JSON.stringify({ error: "invalid user_type" }), {
        status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    if (!/^\d{6}$/.test(pin)) {
      return new Response(JSON.stringify({ error: "PIN must be 6 digits" }), {
        status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    const table = user_type === "client" ? "vito_users" : 
               user_type === "driver" ? "vito_drivers" : "vito_admins"

    const supabaseUrl = Deno.env.get("SUPABASE_URL")!
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!

    const userResponse = await fetch(
      supabaseUrl + "/rest/v1/" + table + "?username=eq." + encodeURIComponent(username) + "&select=id,username,pin_hash,pin_failed_attempts,pin_locked_until,is_suspended",
      { headers: { apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey } }
    )

    const users = await userResponse.json()

    if (!users || users.length === 0) {
      return new Response(JSON.stringify({ error: "Invalid username or PIN" }), {
        status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    const user = users[0]

    if (user.is_suspended) {
      return new Response(JSON.stringify({ error: "Account suspended" }), {
        status: 403, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    if (user.pin_locked_until && new Date(user.pin_locked_until) > new Date()) {
      return new Response(JSON.stringify({ error: "Account locked. Try again later." }), {
        status: 423, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    const pinValid = pin === user.pin_hash
    if (!pinValid) {
      const newAttempts = (user.pin_failed_attempts || 0) + 1
      let lockUntil = null
      if (newAttempts >= 5) {
        const lockDate = new Date()
        lockDate.setMinutes(lockDate.getMinutes() + 5)
        lockUntil = lockDate.toISOString()
      }

      await fetch(
        supabaseUrl + "/rest/v1/" + table + "?id=eq." + user.id,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey },
          body: JSON.stringify({ pin_failed_attempts: newAttempts, pin_locked_until: lockUntil })
        }
      )

      return new Response(JSON.stringify({ error: "Invalid username or PIN" }), {
        status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" }
      })
    }

    await fetch(
      supabaseUrl + "/rest/v1/" + table + "?id=eq." + user.id,
      {
        method: "PATCH",
        headers: { "Content-Type": "application/json", apikey: supabaseKey, "Authorization": "Bearer " + supabaseKey },
        body: JSON.stringify({ pin_failed_attempts: 0, pin_locked_until: null })
      }
    )

    const payload = {
      sub: user.id,
      username: user.username,
      type: user_type,
      exp: Math.floor(Date.now() / 1000) + 30 * 24 * 60 * 60,
    }

    const token = btoa(JSON.stringify(payload))

    return new Response(JSON.stringify({
      success: true,
      token,
      user_id: user.id,
      username: user.username,
      user_type
    }), {
      headers: { ...corsHeaders, "Content-Type": "application/json" }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" }
    })
  }
})

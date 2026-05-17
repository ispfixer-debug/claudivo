// Shared auth utilities for Edge Functions
// Per PLAN.md §8.1

import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

// Create Supabase client with service role key
export function createSupabaseClient(supabaseUrl: string, serviceRoleKey: string) {
  return createClient(supabaseUrl, serviceRoleKey, {
    auth: {
      autoRefreshToken: false,
      persistSession: false
    }
  })
}

// Verify JWT and get user
export async function verifyAuth(req: Request, supabaseUrl: string, anonKey: string): Promise<{userId: string, role: string} | null> {
  const authHeader = req.headers.get('Authorization')
  if (!authHeader?.startsWith('Bearer ')) {
    return null
  }
  
  const token = authHeader.slice(7)
  const supabase = createClient(supabaseUrl, anonKey)
  
  const { data: { user }, error } = await supabase.auth.getUser(token)
  if (error || !user) {
    return null
  }
  
  // Get user role from vito_users
  const { data: userData } = await supabase
    .from('vito_users')
    .select('id, role')
    .eq('id', user.id)
    .single()
  
  return userData ? { userId: userData.id, role: userData.role } : null
}

// Check service role authorization
export function verifyServiceRole(req: Request, serviceRoleKey: string): boolean {
  const authHeader = req.headers.get('Authorization')
  return authHeader === `Bearer ${serviceRoleKey}`
}

// Parse JSON body safely
export async function parseJsonBody<T>(req: Request): Promise<T | null> {
  try {
    return await req.json() as T
  } catch {
    return null
  }
}

// Standard response helpers
export function jsonResponse(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: { 'Content-Type': 'application/json' }
  })
}

export function errorResponse(message: string, status = 400): Response {
  return jsonResponse({ error: message }, status)
}

export function successResponse(data: unknown): Response {
  return jsonResponse({ data }, 200)
}
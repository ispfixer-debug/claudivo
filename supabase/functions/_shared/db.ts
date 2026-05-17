// Shared database utilities
import { createClient, SupabaseClient } from 'https://esm.sh/@supabase/supabase-js@2'

export function createDbClient(supabaseUrl: string, serviceRoleKey: string): SupabaseClient {
  return createClient(supabaseUrl, serviceRoleKey, {
    auth: { autoRefreshToken: false, persistSession: false }
  })
}

// Execute a database function with atomic transaction
export async function executeAtomic(
  client: SupabaseClient,
  fn: string,
  args: Record<string, unknown>
) {
  const { data, error } = await client.rpc(fn, args)
  if (error) {
    throw new Error(error.message)
  }
  return data
}

// Query single row or throw
export async function queryOne<T>(
  client: SupabaseClient,
  from: string,
  filters: Record<string, unknown>
): Promise<T | null> {
  const query = client.from(from).select('*')
  
  for (const [key, value] of Object.entries(filters)) {
    query.eq(key, value)
  }
  
  const { data, error } = await query.single()
  if (error) {
    return null
  }
  return data as T
}

// Insert and return
export async function insertAndGet<T>(
  client: SupabaseClient,
  table: string,
  row: Record<string, unknown>
): Promise<T> {
  const { data, error } = await client.from(table).insert(row).select().single()
  if (error) {
    throw new Error(error.message)
  }
  return data as T
}

// Update and return
export async function updateAndGet<T>(
  client: SupabaseClient,
  table: string,
  id: string,
  updates: Record<string, unknown>
): Promise<T> {
  const { data, error } = await client
    .from(table)
    .update(updates)
    .eq('id', id)
    .select()
    .single()
  if (error) {
    throw new Error(error.message)
  }
  return data as T
}
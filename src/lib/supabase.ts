import { createClient } from "@supabase/supabase-js";

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || "https://oqtmrnemjfhdvdnjvjdy.supabase.co";
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9xdG1ybmVtamZoZHZkbmp2amR5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA1MTYwODksImV4cCI6MjA5NjA5MjA4OX0.pnXwVEDVxSkOhCPxMKcxRIThITSCRP84VWtqDu6MADk";

export const supabase = createClient(supabaseUrl, supabaseAnonKey);

// Server-side client (uses service role key — never expose on client)
export function createServerClient() {
  return createClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.SUPABASE_SERVICE_ROLE_KEY!
  );
}

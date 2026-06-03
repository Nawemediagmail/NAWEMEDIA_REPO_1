import { NextResponse } from "next/server";

export async function GET() {
  const envs = Object.keys(process.env).filter(key => key.includes("SUPABASE") || key.includes("NEXT_PUBLIC"));
  
  return NextResponse.json({ 
    available_env_keys: envs,
    message: envs.length === 0 ? "No related env vars found" : "Related env vars found"
  });
}

import { NextResponse } from "next/server";
import { createClient } from "@supabase/supabase-js";
import { env, validateEnv } from "@/lib/env";

export async function POST(req: Request) {
  try {
    const data = await req.json();
    
    try {
      validateEnv();
    } catch (e: any) {
      return NextResponse.json({ error: e.message }, { status: 500 });
    }

    const supabase = createClient(env.supabaseUrl!, env.supabaseServiceRoleKey!);

    const { error } = await supabase
      .from("onboarding_submissions")
      .insert([{ data }]);

    if (error) {
      return NextResponse.json({ error: error.message }, { status: 500 });
    }

    return NextResponse.json({ success: true });
  } catch (e: any) {
    return NextResponse.json({ error: e.message }, { status: 500 });
  }
}

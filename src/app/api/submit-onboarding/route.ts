import { NextRequest, NextResponse } from "next/server";
import { createClient } from "@supabase/supabase-js";

export async function POST(req: NextRequest) {
  try {
    const data = await req.json();

    if (!data.artistName || !data.bookingEmail) {
      return NextResponse.json(
        { error: "Faltan campos obligatorios" },
        { status: 400 }
      );
    }

    const url = "https://oqtmrnemjfhdvdnjvjdy.supabase.co";
    const key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9xdG1ybmVtamZoZHZkbmp2amR5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA1MTYwODksImV4cCI6MjA5NjA5MjA4OX0.pnXwVEDVxSkOhCPxMKcxRIThITSCRP84VWtqDu6MADk";

    const supabase = createClient(url, key);

    const { error } = await supabase
      .from("onboarding_submissions")
      .insert([{ data }]);

    if (error) {
      console.error("Supabase error:", error);
      return NextResponse.json({ error: error.message }, { status: 500 });
    }

    return NextResponse.json({ success: true });
  } catch (err) {
    console.error("Submit error:", err);
    const message = err instanceof Error ? err.message : "Error interno";
    return NextResponse.json({ error: message }, { status: 500 });
  }
}

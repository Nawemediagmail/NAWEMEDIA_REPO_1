import { NextRequest, NextResponse } from "next/server";
import { createServerClient } from "@/lib/supabase";

export async function POST(req: NextRequest) {
  try {
    const data = await req.json();

    if (!data.artistName || !data.bookingEmail) {
      return NextResponse.json(
        { error: "Faltan campos obligatorios" },
        { status: 400 }
      );
    }

    const supabase = createServerClient();

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
    return NextResponse.json({ error: "Error interno" }, { status: 500 });
  }
}

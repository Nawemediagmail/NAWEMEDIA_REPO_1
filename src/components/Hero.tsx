"use client";
import { useEffect, useRef } from "react";

interface HeroProps {
  heroLead?: string;
  kicker?: string;
  tagline?: string;
  bookingEmail?: string;
}

export default function Hero({ heroLead, kicker, tagline, bookingEmail }: HeroProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    const rm = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    let ps: { x: number; y: number; r: number; s: number; a: number }[] = [];
    let w = 0, h = 0;

    function resize() {
      const ratio = Math.min(window.devicePixelRatio || 1, 2);
      w = canvas!.offsetWidth;
      h = canvas!.offsetHeight;
      canvas!.width = w * ratio;
      canvas!.height = h * ratio;
      ctx!.setTransform(ratio, 0, 0, ratio, 0, 0);
      ps = Array.from({ length: Math.min(90, Math.floor(w / 15)) }, () => ({
        x: Math.random() * w, y: Math.random() * h,
        r: Math.random() * 1.7 + .35, s: Math.random() * .45 + .1, a: Math.random() * .5 + .15
      }));
    }

    function draw() {
      if (!ctx || !canvas) return;
      ctx.clearRect(0, 0, w, h);
      ps.forEach(p => {
        p.y -= p.s; p.x += Math.sin(p.y * .01) * .25;
        if (p.y < -8) { p.y = h + 8; p.x = Math.random() * w; }
        const g = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.r * 7);
        g.addColorStop(0, `rgba(246,207,131,${p.a})`);
        g.addColorStop(.5, `rgba(212,0,212,${p.a * .45})`);
        g.addColorStop(1, "rgba(212,0,212,0)");
        ctx.fillStyle = g;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r * 7, 0, Math.PI * 2);
        ctx.fill();
      });
      requestAnimationFrame(draw);
    }

    resize();
    window.addEventListener("resize", resize);
    if (!rm) draw();
    return () => { window.removeEventListener("resize", resize); };
  }, []);

  return (
    <header className="relative min-h-svh grid items-center overflow-hidden pt-28 pb-16 px-[clamp(1rem,5vw,4rem)]" id="top">
      <canvas ref={canvasRef} className="absolute inset-0 w-full h-full opacity-85 pointer-events-none" aria-hidden="true" />
      <div className="absolute inset-0 pointer-events-none"
        style={{
          background: "linear-gradient(115deg,rgba(10,10,10,.92) 0%,rgba(10,10,10,.62) 42%,rgba(10,10,10,.28) 100%),radial-gradient(circle at 72% 34%,rgba(212,0,212,.38),transparent 24rem),radial-gradient(circle at 80% 78%,rgba(246,207,131,.16),transparent 20rem)"
        }} />
      <div className="relative z-10 w-full" style={{maxWidth:"var(--max-w)",margin:"0 auto"}}>
        <div className="grid gap-8 items-center lg:grid-cols-[.95fr_420px]">
          <div style={{maxWidth:720}}>
            <div className="inline-flex items-center gap-3 px-4 py-1.5 pr-5 rounded-full text-xs font-extrabold tracking-widest uppercase mb-6"
              style={{border:"1px solid rgba(246,207,131,.35)",background:"rgba(255,255,255,.06)",backdropFilter:"blur(14px)",color:"var(--gold)"}}>
              <svg width="44" height="44" viewBox="0 0 44 44" fill="none">
                <polygon points="22,3 29,15 43,17 33,29 35,43 22,36 9,43 11,29 1,17 15,15" fill="var(--gold)" opacity="0.7"/>
              </svg>
              {kicker || "Official Electronic Press Kit"}
            </div>
            <p className="uppercase tracking-[.26em] font-black text-xs mb-3" style={{color:"var(--rose)"}}>
              {kicker || "Hamburg based Colombian DJ"}
            </p>
            <h1 className="m-0 leading-[.84] tracking-[-.09em] uppercase"
              style={{fontSize:"clamp(3.2rem,13vw,9.8rem)",textShadow:"0 0 42px rgba(212,0,212,.22)"}}>
              <span className="block bg-gradient-to-r from-white via-[#ff9bcf] via-[#ff44ef] to-[#f6cf83] bg-clip-text text-transparent">SOFI</span>
              <span className="block bg-gradient-to-r from-white via-[#ff9bcf] via-[#ff44ef] to-[#f6cf83] bg-clip-text text-transparent">FITVIBES</span>
            </h1>
            <p className="mt-4 text-lg font-extrabold uppercase tracking-[.18em]">{tagline || "Afro House · Tribal · Circuit"}</p>
            <p className="max-w-[58ch] mt-5 mb-7 text-base leading-relaxed" style={{color:"var(--muted)"}}>
              {heroLead}
            </p>
            <div className="flex flex-wrap gap-3">
              <a className="btn-primary" href={`mailto:${bookingEmail || "nawemedia@gmail.com"}?subject=Booking%20SOFIFITIVIBES`}>Book Now</a>
              <a className="btn" href="#links">Listen &amp; Follow</a>
            </div>
          </div>
          <div className="relative min-h-[360px] lg:min-h-[460px] rounded-[34px] overflow-hidden border border-white/10 shadow-[0_24px_70px_rgba(212,0,212,.25)]">
            <div className="absolute -inset-[30%] z-[-1]" style={{background:"conic-gradient(from 120deg,transparent,rgba(212,0,212,.28),transparent,rgba(246,207,131,.18),transparent)",animation:"spin 13s linear infinite"}} />
            <img src="/assets/SOFI_1.2_.png" alt="SOFIFITVIBES DJ portrait"
              className="w-full h-full min-h-[360px] lg:min-h-[460px] object-cover object-[52%_22%] saturate-[1.12] contrast-[1.04]"
              onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }} />
          </div>
        </div>
      </div>
    </header>
  );
}

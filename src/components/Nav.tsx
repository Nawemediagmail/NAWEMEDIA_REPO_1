"use client";
import Link from "next/link";

export default function Nav() {
  return (
    <nav className="fixed z-30 top-0 left-0 right-0 flex items-center justify-between gap-4 px-[clamp(1rem,4vw,3rem)] py-3.5"
      style={{background:"linear-gradient(180deg,rgba(5,4,5,.92),rgba(5,4,5,.45))",backdropFilter:"blur(18px)",borderBottom:"1px solid rgba(255,255,255,.08)"}}>
      <Link href="#top" className="flex items-center gap-3"
        style={{filter:"drop-shadow(0 0 16px rgba(212,0,212,.55))"}}>
        <img src="/assets/logo.png" alt="SOFIFITVIBES" className="h-11 w-auto object-contain" />
      </Link>
      <div className="hidden md:flex items-center gap-5 text-sm uppercase tracking-widest" style={{color:"var(--muted)"}}>
        <Link href="#bio" className="hover:text-white transition-colors">Bio</Link>
        <Link href="#music" className="hover:text-white transition-colors">Music</Link>
        <Link href="#video" className="hover:text-white transition-colors">Video</Link>
        <Link href="#shows" className="hover:text-white transition-colors">Shows</Link>
        <Link href="#gallery" className="hover:text-white transition-colors">Gallery</Link>
        <Link href="#links" className="hover:text-white transition-colors">Links</Link>
      </div>
      <div className="flex items-center gap-2.5">
        <a className="btn-primary" href="mailto:nawemedia@gmail.com?subject=Booking%20SOFIFITIVIBES">Book</a>
        <a href="/admin" className="w-[42px] h-[42px] flex items-center justify-center rounded-full text-xl no-underline" style={{border:"1px solid rgba(255,255,255,.14)",background:"rgba(255,255,255,.06)"}} aria-label="Admin">&#9881;</a>
      </div>
    </nav>
  );
}

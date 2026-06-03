export default function Footer() {
  return (
    <footer className="w-full py-12 mt-14 text-center flex flex-col items-center gap-3 text-sm"
      style={{borderTop:"1px solid rgba(255,255,255,.07)",color:"var(--color-muted)"}}>
      <div className="uppercase tracking-widest text-xs font-extrabold"
        style={{color:"var(--color-gold)",filter:"drop-shadow(0 0 18px rgba(246,207,131,.2))"}}>
        SOFIFITVIBES
      </div>
      <div className="uppercase tracking-widest text-[10px] font-bold opacity-70">
        ELECTRONIC PRESS KIT MADE BY: <a href="https://nawemedia.pro" target="_blank" rel="noopener" className="underline underline-offset-2" style={{color:"var(--text)"}}>NAWEMEDIA.PRO</a>
      </div>
      <div className="flex flex-wrap justify-center items-center gap-x-4 gap-y-2 mt-2 opacity-80">
        <span className="text-[11px] font-medium">© NAWEMEDIA</span>
        
        <a href="https://instagram.com/nawemedia.pro" target="_blank" rel="noopener" className="flex items-center gap-1.5 hover:text-white transition-colors">
          <svg viewBox="0 0 64 64" fill="none" className="w-3.5 h-3.5" aria-hidden="true">
            <path d="M16 32a16 16 0 1 0 32 0 16 16 0 0 0-32 0zM44 20a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" strokeWidth="3.5" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          <span className="text-[11px]">instagram.com/nawemedia.pro</span>
        </a>

        <a href="mailto:nawemedia@gmail.com" className="flex items-center gap-1.5 hover:text-white transition-colors">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-3.5 h-3.5">
            <rect width="20" height="16" x="2" y="4" rx="2" />
            <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
          </svg>
          <span className="text-[11px]">nawemedia@gmail.com</span>
        </a>

        <a href="tel:+56959985061" className="flex items-center gap-1.5 hover:text-white transition-colors">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-3.5 h-3.5">
            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
          </svg>
          <span className="text-[11px]">+56 9 59985061</span>
        </a>
      </div>
    </footer>
  );
}

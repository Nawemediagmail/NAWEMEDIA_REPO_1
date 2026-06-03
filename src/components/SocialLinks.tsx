interface Social {
  id?: number;
  platform: string;
  url: string;
  label: string;
}

interface SocialLinksProps {
  socials: Social[];
}

export default function SocialLinks({ socials }: SocialLinksProps) {
  const icons: Record<string, string> = {
    soundcloud: "M16 8v48M24 14v36M32 20v28M40 26v20",
    instagram: "M16 32a16 16 0 1 0 32 0 16 16 0 0 0-32 0zM44 20a4 4 0 1 0 0-8 4 4 0 0 0 0 8z",
    youtube: "M8 12v40l40-20z",
    spotify: "M24 24c6 0 11 3 14 6l-4 4c-2-2-6-4-10-4s-8 2-10 4l-4-4c3-3 8-6 14-6zM24 14c10 0 18 5 23 10l-4 4c-4-4-11-8-19-8s-15 4-19 8l-4-4c5-5 13-10 23-10z",
    twitter: "M8 8l18 24L8 56h8l14-16 14 16h12L36 30l22-22h-8L30 23 17 8H8z",
    facebook: "M32 8C19 8 8 19 8 32s11 24 24 24 24-11 24-24S45 8 32 8zM36 26h-4v-2c0-2 1-2 3-2h2v-5l-4-1c-4 0-7 3-7 8v2h-4v5h4v16h6V31h4l1-5z",
    tiktok: "M42 8H30v26c0 5-4 9-9 9s-9-4-9-9 4-9 9-9h1v-6h-1c-8 0-15 6-15 15s7 15 15 15 15-7 15-15V23a17 17 0 0 0 10 4v-6c-2 0-5-1-7-2-2-2-3-5-3-8l-2-3z",
    twitch: "M12 8v32l10 6h10l6-6h10l16-16V8H12zM52 26l-6 6H36l-6 6V32H24V14h28v12z",
    linktree: "M32 8L16 32h12v16h8V32h12L32 8z",
  };

  return (
    <section className="section" id="links">
      <div className="section-inner">
        <p className="section-label">Connect</p>
        <h2 className="section-title">Follow <em>&amp; stream</em></h2>
        <div className="grid gap-3 mt-8 sm:grid-cols-2 lg:grid-cols-3">
          {(socials || []).map((s, i) => (
            <a key={s.id || i} href={s.url} target="_blank" rel="noopener" className="social-link">
              <svg viewBox="0 0 64 64" fill="none" className="w-6 h-6 shrink-0" aria-hidden="true">
                {icons[s.platform.toLowerCase()]?.split(" ").map((d, j) => (
                  <path key={j} d={d} stroke="currentColor" strokeWidth="3.5" strokeLinecap="round" strokeLinejoin="round" />
                )) || <circle cx="32" cy="32" r="24" stroke="currentColor" strokeWidth="3.5" />}
              </svg>
              <div>
                <div className="text-sm font-extrabold uppercase tracking-wider">{s.label || s.platform}</div>
                <div className="text-xs" style={{color:"var(--muted)"}}>@{s.platform}</div>
              </div>
            </a>
          ))}
        </div>
        <div className="mt-12 flex justify-center">
          <a href="https://www.dropbox.com/scl/fo/wqfazafjmc04mgmo9jwur/ADLYHiIXi6j-9unUPUSDGeA?rlkey=2x2xmp2qcv6zkuhobpez76y3k&e=2&st=lg9frh89&dl=0" 
             target="_blank" 
             rel="noopener" 
             className="btn-primary px-8 py-4 text-sm font-black uppercase tracking-widest"
             style={{background:"var(--color-gold)",color:"#000",filter:"drop-shadow(0 0 15px rgba(212,0,212,.4))"}}>
            Download Promo Pack
          </a>
        </div>
      </div>
    </section>
  );
}

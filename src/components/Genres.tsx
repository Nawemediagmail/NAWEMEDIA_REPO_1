export default function Genres() {
  const items = [
    { title: "Afro House", desc: "Warm percussion, hypnotic grooves and an elegant pulse built for global dancefloors.", icon: "M12 42c10-2 11-18 20-20 11-3 13 18 25 8M13 50c14 0 20-8 30-5 4 1 7 4 10 7 M46 18a6 6 0 1 0 0-12 6 6 0 0 0 0 12z" },
    { title: "Tribal", desc: "Driving drums, ritual energy and high-impact transitions for a powerful room connection.", icon: "M16 14v36M32 8v48M48 14v36M10 24h44M10 40h44" },
    { title: "Circuit", desc: "Big-room intensity, queer nightlife spirit and peak-time momentum from start to finish.", icon: "M32 8l7 16 17 2-13 11 4 17-15-9-15 9 4-17L8 26l17-2 7-16zM24 33h16" },
  ];

  return (
    <section className="section" id="genres">
      <div className="section-inner">
        <p className="section-label">Sound Identity</p>
        <h2 className="section-title">Rhythm, heat <em>&amp; movement</em></h2>
        <div className="grid gap-4 mt-8 md:grid-cols-3">
          {items.map((item, i) => (
            <article key={i} className="genre-card">
              <svg viewBox="0 0 64 64" fill="none" aria-hidden="true" className="w-11 h-11" style={{color:"var(--gold)",filter:"drop-shadow(0 0 18px rgba(246,207,131,.28))"}}>
                {item.icon.split(" ").map((d, j) => {
                  if (d.startsWith("M") || d.startsWith("C") || d.startsWith("L")) return <path key={j} d={d} stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round" />;
                  return null;
                })}
              </svg>
              <h3 className="mt-5 mb-2 text-2xl uppercase tracking-tight">{item.title}</h3>
              <p style={{color:"var(--muted)"}}>{item.desc}</p>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}

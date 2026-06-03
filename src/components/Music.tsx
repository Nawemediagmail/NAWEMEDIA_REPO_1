interface MusicTrack {
  id?: number;
  title: string;
  type: string;
  embed_url: string;
  sort_order?: number;
}

interface MusicProps {
  tracks: MusicTrack[];
}

export default function Music({ tracks }: MusicProps) {
  if (!tracks || tracks.length === 0) {
    return (
      <section className="section" id="music">
        <div className="section-inner">
          <p className="section-label">Sets &amp; Releases</p>
          <h2 className="section-title">Listen to <em>the mix</em></h2>
          <p style={{color:"var(--muted)"}}>No tracks available yet.</p>
        </div>
      </section>
    );
  }

  return (
    <section className="section" id="music">
      <div className="section-inner">
        <p className="section-label">Sets &amp; Releases</p>
        <h2 className="section-title">Listen to <em>the mix</em></h2>
        <div className="flex flex-col gap-4 mt-8">
          {tracks.sort((a, b) => (a.sort_order || 0) - (b.sort_order || 0)).map((track, i) => (
            <div key={track.id || i} className="music-card">
              <div className="music-head">
                <div className="music-title">{track.title}</div>
                <span className="music-badge">{track.type === "set" ? "Set" : "Release"}</span>
              </div>
              <iframe scrolling="no" src={track.embed_url} allow="autoplay" className="w-full h-[166px] border-none rounded-xl" />
            </div>
          ))}
        </div>
        <div className="mt-8 flex justify-center">
          <a href="https://soundcloud.com/sofifitvibes" target="_blank" rel="noopener" className="btn">
            More on SoundCloud &rarr;
          </a>
        </div>
      </div>
    </section>
  );
}

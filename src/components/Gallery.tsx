"use client";
import { useState } from "react";

interface GalleryImage {
  id?: number;
  image_url: string;
  caption: string;
  sort_order?: number;
}

interface GalleryProps {
  images: GalleryImage[];
}

export default function Gallery({ images }: GalleryProps) {
  const [lightboxIndex, setLightboxIndex] = useState<number | null>(null);

  if (!images || images.length === 0) return null;

  const open = (i: number) => setLightboxIndex(i);
  const close = () => setLightboxIndex(null);
  const prev = () => setLightboxIndex(i => i !== null ? (i - 1 + images.length) % images.length : null);
  const next = () => setLightboxIndex(i => i !== null ? (i + 1) % images.length : null);

  return (
    <section className="section" id="gallery">
      <div className="section-inner">
        <p className="section-label">Press Photos</p>
        <h2 className="section-title">Gallery <em>&amp; visuals</em></h2>
        <div className="flex flex-wrap gap-2 mt-6 mb-4">
          <span className="btn" style={{minHeight:36,padding:".4rem .85rem",fontSize:".72rem",cursor:"default"}}>Official Photos</span>
          <span className="btn" style={{minHeight:36,padding:".4rem .85rem",fontSize:".72rem",cursor:"default"}}>Press Ready</span>
        </div>
        <div className="gallery-grid">
          {images.map((img, i) => (
            <button key={img.id || i} type="button" className="gallery-item" onClick={() => open(i)}>
              <img src={img.image_url} alt={img.caption} loading="lazy"
                onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }} />
              <span className="gallery-caption">{img.caption}</span>
            </button>
          ))}
        </div>
      </div>

      {lightboxIndex !== null && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4"
          style={{background:"rgba(0,0,0,.86)",backdropFilter:"blur(16px)"}}
          onClick={close}>
          <button className="absolute top-4 right-4 z-10 w-10 h-10 flex items-center justify-center rounded-full text-xl"
            style={{border:"1px solid rgba(255,255,255,.14)",background:"rgba(255,255,255,.06)"}}
            aria-label="Close">&times;</button>
          <button className="absolute left-4 top-1/2 -translate-y-1/2 z-10 px-4 py-6 text-lg"
            style={{background:"rgba(212,0,212,.08)",border:"1px solid rgba(212,0,212,.18)",color:"var(--muted)"}}
            onClick={(e) => { e.stopPropagation(); prev(); }}>&lsaquo;</button>
          <img src={images[lightboxIndex]?.image_url} alt={images[lightboxIndex]?.caption}
            className="max-w-[96vw] max-h-[86vh] object-contain rounded-[22px] shadow-[0_30px_90px_rgba(0,0,0,.5)]"
            onClick={e => e.stopPropagation()} />
          <button className="absolute right-4 top-1/2 -translate-y-1/2 z-10 px-4 py-6 text-lg"
            style={{background:"rgba(212,0,212,.08)",border:"1px solid rgba(212,0,212,.18)",color:"var(--muted)"}}
            onClick={(e) => { e.stopPropagation(); next(); }}>&rsaquo;</button>
        </div>
      )}
    </section>
  );
}

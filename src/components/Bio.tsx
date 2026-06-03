"use client";
import { useState } from "react";

interface BioBlock {
  language: string;
  content: string;
}

interface BioProps {
  bios: BioBlock[];
}

export default function Bio({ bios }: BioProps) {
  const [lang, setLang] = useState("en");
  const current = bios?.find(b => b.language === lang);
  const paragraphs = (current?.content || "").split(/\n\s*\n/).filter(Boolean);

  return (
    <section className="section" id="bio">
      <div className="section-inner">
        <div className="bio-grid">
          <div className="relative rounded-[28px] overflow-hidden border border-white/10 shadow-[0_24px_70px_rgba(0,0,0,.34)]"
            style={{background:"linear-gradient(140deg,rgba(212,0,212,.18),rgba(255,255,255,.05))"}}>
            <img src="/assets/SOFI_5.2_.png" alt="SOFIFITVIBES artist photo"
              className="w-full h-[420px] lg:h-[520px] object-cover object-[center_16%]"
              onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }} />
          </div>
          <div className="bio-copy">
            <p className="section-label">Artist Bio</p>
            <h2 className="section-title">Energy behind <em>the decks</em></h2>
            <div className="language-switcher">
              {["en", "de", "es"].map(l => (
                <button key={l} type="button"
                  className={`lang-btn ${lang === l ? "active" : ""}`}
                  onClick={() => setLang(l)}>{l.toUpperCase()}</button>
              ))}
            </div>
            <div className="bio-text">
              {paragraphs.map((p, i) => <p key={i}>{p}</p>)}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

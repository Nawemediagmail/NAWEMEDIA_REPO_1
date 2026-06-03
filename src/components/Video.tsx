export default function Video() {
  return (
    <section className="section" id="video">
      <div className="section-inner">
        <p className="section-label">Live Performance</p>
        <h2 className="section-title">Watch <em>the energy</em></h2>
        <div className="relative w-full mt-8 rounded-[22px] overflow-hidden border border-white/10 shadow-[0_24px_70px_rgba(0,0,0,.4)]"
          style={{paddingBottom: "56.25%"}}>
          <iframe
            src="https://www.youtube-nocookie.com/embed/sNDLevBtPEY"
            title="SOFIFITVIBES DJ"
            className="absolute top-0 left-0 w-full h-full border-none"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          />
        </div>
      </div>
    </section>
  );
}

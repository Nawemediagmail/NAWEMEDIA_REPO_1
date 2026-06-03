interface Show {
  id?: number;
  date: string;
  venue: string;
  city: string;
  country: string;
  type: string;
  status: string;
  ticket_url: string;
}

interface ShowsProps {
  shows: Show[];
}

export default function Shows({ shows }: ShowsProps) {
  const today = new Date().toISOString().split("T")[0];
  const upcoming = (shows || [])
    .filter(s => s.date >= today)
    .sort((a, b) => a.date.localeCompare(b.date));

  return (
    <section className="section" id="shows">
      <div className="section-inner">
        <p className="section-label">Agenda</p>
        <h2 className="section-title">Upcoming <em>shows</em></h2>
        <div className="mt-8">
          {upcoming.length === 0 ? (
            <div className="no-shows">Upcoming shows coming soon — follow on socials to stay updated.</div>
          ) : (
            upcoming.map((s, i) => {
              const d = new Date(s.date + "T12:00:00");
              const dateStr = d.toLocaleDateString("en-US", { day: "2-digit", month: "short", year: "numeric" }).toUpperCase();
              const dayStr = d.toLocaleDateString("en-US", { weekday: "long" });
              const isSO = s.status === "sold-out";
              const isC = s.status === "cancelled";
              return (
                <div key={s.id || i} className="show-item">
                  <div>
                    <div className="show-date">{dateStr}</div>
                    <div className="show-date-day">{dayStr}</div>
                  </div>
                  <div>
                    <div className="show-venue">{s.venue}</div>
                    <div className="show-city">{s.city}, {s.country}</div>
                  </div>
                  <div className="show-type hidden md:block">{s.type}</div>
                  {isC ? (
                    <span className="ticket-cancelled">Cancelled</span>
                  ) : (
                    <a className={`ticket-btn${isSO ? " sold-out" : ""}`}
                      href={s.ticket_url || "#"} target="_blank" rel="noopener">
                      {isSO ? "Sold Out" : "Tickets"}
                    </a>
                  )}
                </div>
              );
            })
          )}
        </div>
      </div>
    </section>
  );
}

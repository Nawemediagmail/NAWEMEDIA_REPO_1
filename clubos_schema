-- ═══════════════════════════════════════════════
-- NAWEMEDIA ClubOS — Database Schema MVP v1.0
-- Stack: Supabase + PostgreSQL
-- Arquitectura: Multi-tenant white-label
-- ═══════════════════════════════════════════════

-- EXTENSIONES
create extension if not exists "uuid-ossp";
create extension if not exists "pgcrypto";

-- ═══════════════════════════════════════════════
-- ENUMS
-- ═══════════════════════════════════════════════
create type venue_country as enum ('CL', 'CO', 'PE', 'AR', 'MX', 'OTHER');
create type payment_gateway as enum ('stripe', 'webpay', 'payU', 'nequi', 'yape', 'kushki');
create type payment_status as enum ('pending', 'paid', 'failed', 'refunded', 'chargeback');
create type ticket_status as enum ('valid', 'used', 'cancelled');
create type staff_role as enum ('owner', 'admin', 'staff', 'rrpp');

-- ═══════════════════════════════════════════════
-- TABLA: venues
-- Root del multi-tenant. Un venue = un cliente SaaS.
-- ═══════════════════════════════════════════════
create table venues (
  id              uuid primary key default gen_random_uuid(),
  slug            text unique not null,          -- subdomain: elclub.clubos.app
  name            text not null,
  owner_id        uuid references auth.users(id) on delete cascade not null,
  logo_url        text,
  banner_url      text,
  primary_color   text default '#FFFFFF',        -- white-label brand
  accent_color    text default '#FF0000',
  country         venue_country not null,
  city            text,
  address         text,
  whatsapp_number text,                          -- para automatizaciones
  instagram_handle text,
  tiktok_handle   text,
  website_url     text,
  -- Pagos
  stripe_account_id    text,                     -- Stripe Connect account_id
  default_gateway      payment_gateway default 'stripe',
  webpay_commerce_code text,                     -- Chile: Transbank
  payu_merchant_id     text,                     -- Colombia
  -- Metadata
  timezone        text default 'America/Santiago',
  is_active       boolean default true,
  plan            text default 'mvp',            -- mvp | pro | enterprise
  plan_expires_at timestamptz,
  created_at      timestamptz default now(),
  updated_at      timestamptz default now()
);

-- ═══════════════════════════════════════════════
-- TABLA: profiles
-- Extiende auth.users para staff del venue
-- ═══════════════════════════════════════════════
create table profiles (
  id           uuid primary key references auth.users(id) on delete cascade,
  venue_id     uuid references venues(id) on delete cascade,
  role         staff_role not null default 'staff',
  display_name text,
  avatar_url   text,
  phone        text,
  is_active    boolean default true,
  created_at   timestamptz default now()
);

-- ═══════════════════════════════════════════════
-- TABLA: events
-- M eventos por venue
-- ═══════════════════════════════════════════════
create table events (
  id            uuid primary key default gen_random_uuid(),
  venue_id      uuid references venues(id) on delete cascade not null,
  name          text not null,
  description   text,
  flyer_url     text,                            -- S3/Supabase Storage
  flyer_video_url text,                          -- motion flyer opcional
  date          timestamptz not null,
  doors_open    timestamptz,
  end_time      timestamptz,
  location_name text,                            -- puede ser distinto al venue
  location_address text,
  genre         text,                            -- techno / reggaeton / etc
  min_age       int default 18,
  dress_code    text,
  published     boolean default false,           -- visible al público
  is_active     boolean default true,
  created_at    timestamptz default now(),
  updated_at    timestamptz default now()
);

-- ═══════════════════════════════════════════════
-- TABLA: ticket_types
-- Tiers de precio por evento (Preventa / VIP / General)
-- ═══════════════════════════════════════════════
create table ticket_types (
  id              uuid primary key default gen_random_uuid(),
  event_id        uuid references events(id) on delete cascade not null,
  name            text not null,                 -- 'Preventa', 'VIP', 'General'
  description     text,
  price           numeric(10,2) not null,
  currency        text not null default 'CLP',   -- CLP / COP / PEN / USD
  quantity_total  int not null,
  quantity_sold   int default 0,
  quantity_reserved int default 0,               -- en proceso de pago
  sale_start      timestamptz,
  sale_end        timestamptz,
  max_per_order   int default 4,
  is_active       boolean default true,
  sort_order      int default 0,
  created_at      timestamptz default now(),
  -- Constraint: no sobrevender
  constraint no_oversell check (quantity_sold + quantity_reserved <= quantity_total)
);

-- ═══════════════════════════════════════════════
-- TABLA: attendees
-- CRM core. K attendees, deduplicados por venue+email.
-- ═══════════════════════════════════════════════
create table attendees (
  id                  uuid primary key default gen_random_uuid(),
  venue_id            uuid references venues(id) on delete cascade not null,
  email               text not null,
  phone_whatsapp      text,
  first_name          text,
  last_name           text,
  instagram           text,
  tiktok              text,
  date_of_birth       date,
  -- Consentimiento (CRÍTICO para monetización de datos futura)
  consent_marketing   boolean default false,
  consent_data_sharing boolean default false,    -- cláusula sponsors/marcas
  consent_at          timestamptz,
  consent_ip          text,
  -- Stats CRM
  total_events        int default 0,
  total_spent         numeric(12,2) default 0,
  last_event_at       timestamptz,
  -- Metadata
  source              text default 'organic',    -- organic / rrpp / paid
  tags                text[],                    -- para segmentación
  created_at          timestamptz default now(),
  updated_at          timestamptz default now(),
  -- Unicidad: mismo email, mismo venue = mismo registro
  unique(venue_id, email)
);

-- ═══════════════════════════════════════════════
-- TABLA: orders
-- Transacción de compra (1 order puede tener N tickets)
-- ═══════════════════════════════════════════════
create table orders (
  id                  uuid primary key default gen_random_uuid(),
  venue_id            uuid references venues(id) not null,
  event_id            uuid references events(id) not null,
  attendee_id         uuid references attendees(id) not null,
  ticket_type_id      uuid references ticket_types(id) not null,
  quantity            int not null default 1,
  unit_price          numeric(10,2) not null,
  total_amount        numeric(10,2) not null,
  currency            text not null,
  -- Pago
  payment_status      payment_status not null default 'pending',
  payment_gateway     payment_gateway not null,
  payment_reference   text,                      -- ID externo (Stripe PI, Webpay token)
  payment_metadata    jsonb,                     -- respuesta raw del gateway
  -- Timestamps
  created_at          timestamptz default now(),
  paid_at             timestamptz,
  expires_at          timestamptz default (now() + interval '15 minutes'),
  -- RRPP tracking (Fase 2)
  referred_by         uuid references profiles(id)
);

-- ═══════════════════════════════════════════════
-- TABLA: tickets
-- QR individual por asistente. Uno por persona.
-- ═══════════════════════════════════════════════
create table tickets (
  id              uuid primary key default gen_random_uuid(),
  order_id        uuid references orders(id) not null,
  event_id        uuid references events(id) not null,
  attendee_id     uuid references attendees(id) not null,
  ticket_type_id  uuid references ticket_types(id) not null,
  -- QR
  qr_code         text unique not null default encode(gen_random_bytes(16), 'hex'),
  qr_url          text,                          -- URL pública del ticket digital
  status          ticket_status not null default 'valid',
  -- Validación en puerta
  scanned_at      timestamptz,
  scanned_by      uuid references profiles(id),  -- staff que escaneó
  scan_location   text,                          -- puerta 1 / puerta 2
  -- Metadata
  created_at      timestamptz default now()
);

-- ═══════════════════════════════════════════════
-- TABLA: automations_log
-- Registro de WhatsApp/email enviados (Make/Resend)
-- ═══════════════════════════════════════════════
create table automations_log (
  id              uuid primary key default gen_random_uuid(),
  venue_id        uuid references venues(id) not null,
  attendee_id     uuid references attendees(id),
  ticket_id       uuid references tickets(id),
  type            text not null,                 -- 'ticket_confirmation' / 'reminder' / 'retargeting'
  channel         text not null,                 -- 'whatsapp' / 'email'
  status          text not null,                 -- 'sent' / 'failed' / 'delivered'
  external_id     text,                          -- ID de Make/Resend
  sent_at         timestamptz default now()
);

-- ═══════════════════════════════════════════════
-- ÍNDICES (performance)
-- ═══════════════════════════════════════════════
create index idx_events_venue_id on events(venue_id);
create index idx_events_date on events(date);
create index idx_tickets_qr_code on tickets(qr_code);
create index idx_tickets_event_id on tickets(event_id);
create index idx_orders_attendee_id on orders(attendee_id);
create index idx_attendees_venue_email on attendees(venue_id, email);
create index idx_attendees_whatsapp on attendees(phone_whatsapp);

-- ═══════════════════════════════════════════════
-- TRIGGERS: updated_at automático
-- ═══════════════════════════════════════════════
create or replace function update_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

create trigger venues_updated_at before update on venues
  for each row execute function update_updated_at();
create trigger events_updated_at before update on events
  for each row execute function update_updated_at();
create trigger attendees_updated_at before update on attendees
  for each row execute function update_updated_at();

-- TRIGGER: actualizar quantity_sold en ticket_types al confirmar pago
create or replace function update_ticket_count()
returns trigger as $$
begin
  if new.payment_status = 'paid' and old.payment_status != 'paid' then
    update ticket_types
    set quantity_sold = quantity_sold + new.quantity,
        quantity_reserved = greatest(0, quantity_reserved - new.quantity)
    where id = new.ticket_type_id;
    
    -- Actualizar stats del CRM
    update attendees
    set total_events = total_events + 1,
        total_spent = total_spent + new.total_amount,
        last_event_at = now()
    where id = new.attendee_id;
  end if;
  return new;
end;
$$ language plpgsql;

create trigger order_paid_trigger after update on orders
  for each row execute function update_ticket_count();

-- ═══════════════════════════════════════════════
-- ROW LEVEL SECURITY (RLS)
-- ═══════════════════════════════════════════════
alter table venues enable row level security;
alter table profiles enable row level security;
alter table events enable row level security;
alter table ticket_types enable row level security;
alter table attendees enable row level security;
alter table orders enable row level security;
alter table tickets enable row level security;
alter table automations_log enable row level security;

-- Helper function: venue_id del usuario autenticado
create or replace function my_venue_id()
returns uuid as $$
  select venue_id from profiles where id = auth.uid()
$$ language sql security definer stable;

-- Helper function: role del usuario
create or replace function my_role()
returns staff_role as $$
  select role from profiles where id = auth.uid()
$$ language sql security definer stable;

-- ── VENUES ──────────────────────────────────────
create policy "Owner: full access to own venue" on venues
  for all using (owner_id = auth.uid());

-- ── PROFILES ────────────────────────────────────
create policy "User: read own profile" on profiles
  for select using (id = auth.uid());

create policy "Owner: manage venue staff" on profiles
  for all using (
    venue_id = my_venue_id() and my_role() in ('owner', 'admin')
  );

-- ── EVENTS ──────────────────────────────────────
-- Público: solo eventos publicados y activos
create policy "Public: read published events" on events
  for select using (published = true and is_active = true);

-- Staff: gestión completa de su venue
create policy "Staff: manage venue events" on events
  for all using (venue_id = my_venue_id());

-- ── TICKET TYPES ────────────────────────────────
create policy "Public: read active ticket types" on ticket_types
  for select using (is_active = true);

create policy "Staff: manage ticket types" on ticket_types
  for all using (
    event_id in (select id from events where venue_id = my_venue_id())
  );

-- ── ATTENDEES ───────────────────────────────────
-- Inserción pública (proceso de compra)
create policy "Public: insert attendee on purchase" on attendees
  for insert with check (true);

-- Staff: solo lectura/edición de su venue
create policy "Staff: read venue attendees" on attendees
  for select using (venue_id = my_venue_id());

create policy "Staff: update venue attendees" on attendees
  for update using (venue_id = my_venue_id());

-- ── ORDERS ──────────────────────────────────────
create policy "Public: insert order" on orders
  for insert with check (true);

create policy "Public: read own order" on orders
  for select using (
    attendee_id in (
      select id from attendees where email = (
        select email from auth.users where id = auth.uid()
      )
    )
  );

create policy "Staff: read venue orders" on orders
  for select using (venue_id = my_venue_id());

create policy "Staff: update order status" on orders
  for update using (venue_id = my_venue_id());

-- ── TICKETS ─────────────────────────────────────
create policy "Public: read own ticket by qr" on tickets
  for select using (
    attendee_id in (
      select id from attendees where email = (
        select email from auth.users where id = auth.uid()
      )
    )
  );

create policy "Staff: read venue tickets" on tickets
  for select using (
    event_id in (select id from events where venue_id = my_venue_id())
  );

-- Solo staff puede marcar ticket como usado (escaneo QR)
create policy "Staff: scan/validate ticket" on tickets
  for update using (
    event_id in (select id from events where venue_id = my_venue_id())
  );

-- ── AUTOMATIONS LOG ─────────────────────────────
create policy "Staff: read automation logs" on automations_log
  for select using (venue_id = my_venue_id());

create policy "Service: insert automation logs" on automations_log
  for insert with check (true); -- solo desde service_role / Make webhook

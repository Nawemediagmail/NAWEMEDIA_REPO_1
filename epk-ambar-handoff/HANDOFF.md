# Handoff — dj-ambarlombardi-epk

## Qué está hecho

### Supabase
- Proyecto: `dj-ambarlombardi-epk` — ID: `qbpjuuesgsrotsagorcr`
- URL: `https://qbpjuuesgsrotsagorcr.supabase.co`
- Anon key: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicGp1dWVzZ3Nyb3RzYWdvcmNyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA2MDc1ODksImV4cCI6MjA5NjE4MzU4OX0.CWCOb23nuq0JkHNAJT3CTLWtfT7cLBYdOKOjRZqVZLc`
- Tabla `epk` creada: slug TEXT PK + data JSONB + updated_at TIMESTAMPTZ
- RLS habilitado: SELECT/INSERT/UPDATE públicos

### Archivos listos (en este folder)
- `index.html` — EPK V02 con DataLayer Supabase integrado
- `vercel.json` — config estático + security headers

## Qué falta en la nueva sesión

1. **Push a GitHub**: El repo `Nawemediagmail/dj-ambarlombardi-epk` tiene la carpeta original.
   Necesitas:
   - Copiar `index.html` y `vercel.json` de esta carpeta a la raíz del repo
   - Copiar `Electronic Press Kit (EPK) V02/assets/` → `assets/` en la raíz
   - Crear `.gitignore` (ver abajo)
   - Push a `main`

2. **Vercel**: Crear proyecto `dj-ambarlombardi-epk` importando desde GitHub

3. **Cloudflare**: Apuntar `ambarlombardi.com` → Vercel

## .gitignore
```
.DS_Store
*.log
node_modules/
.vercel/
```

## Vercel team
- Team ID: `team_AJK3ksf0robM6Pr8nJ3tSIrh`
- Team slug: `nawemedia-8661s-projects`

## Cloudflare
- Account ID: `240ee7fd42d8dffd0109eaa20047fb40`
- Domain: `ambarlombardi.com`

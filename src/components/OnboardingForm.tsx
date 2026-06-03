"use client";

import React, { useState } from "react";
import { CheckCircle2, Upload, Globe, Music, User, Calendar, Camera } from "lucide-react";

interface OnboardingFormProps {
  onSubmit: (data: any) => Promise<void>;
  onLoading: (loading: boolean) => void;
}

export default function OnboardingForm({ onSubmit, onLoading }: OnboardingFormProps) {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    // Section 1: Identity
    artistName: "",
    debutYear: "",
    genres: "",
    showsCount: "",
    countriesCount: "",
    // Section 2: Contact & Socials
    bookingEmail: "",
    bookingWhatsapp: "",
    instagram: "",
    soundcloudProfile: "",
    spotify: "",
    youtube: "",
    // Section 3: Bio
    bioEs: "",
    bioEn: "",
    highlights: "",
    // Section 4: Music
    sets: "",
    releases: "",
    // Section 5: Visuals (Links now)
    heroPhotoLink: "",
    pressPhotosLink: "",
    logoBlackLink: "",
    logoColorLink: "",
    // Section 6: Shows
    upcomingShows: "",
    // Section 7: Press Kit
    hdPhotosLink: "",
    riderLink: "",
    extraAssets: "",
    // Section 8: Validation
    deadline: "",
    notes: "",
    confirmation: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const nextStep = () => setStep(s => s + 1);
  const prevStep = () => setStep(s => s - 1);

  const isStepValid = () => {
    if (step === 1) return formData.artistName && formData.genres;
    if (step === 2) return formData.bookingEmail && formData.instagram && formData.soundcloudProfile;
    if (step === 3) return formData.bioEs;
    if (step === 4) return formData.sets && formData.releases;
    return true;
  };

  const progress = (step / 8) * 100;

  return (
    <div className="w-full max-w-4xl mx-auto p-4 sm:p-6">
      {/* Progress Header */}
      <div className="mb-8 text-center">
        <div className="flex justify-between items-end mb-2">
          <span className="text-xs font-bold uppercase tracking-widest text-slate-400">
            Step {step} of 8
          </span>
          <span className="text-xs font-bold text-gold uppercase tracking-widest">
            {Math.round(progress)}% Complete
          </span>
        </div>
        <div className="h-1.5 w-full bg-white/10 rounded-full overflow-hidden">
          <div 
            className="h-full bg-gradient-to-r from-blue-500 via-purple-500 to-amber-500 transition-all duration-500" 
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-[40px] p-6 sm:p-10 backdrop-blur-xl shadow-2xl">
        {step === 1 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-gold/20 text-gold">
                <User size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Identidad del Artista</h2>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Nombre Artístico *</label>
                <input name="artistName" value={formData.artistName} onChange={handleChange} className="input-field" placeholder="Ej. DJ NAWEL" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Año de Debut</label>
                <input name="debutYear" value={formData.debutYear} onChange={handleChange} className="input-field" placeholder="Ej. 2020" />
              </div>
              <div className="flex flex-col gap-2 sm:col-span-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Géneros Principales *</label>
                <input name="genres" value={formData.genres} onChange={handleChange} className="input-field" placeholder="Ej. Afro House, Tribal, Circuit" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Shows Aproximados</label>
                <input name="showsCount" value={formData.showsCount} onChange={handleChange} className="input-field" placeholder="Ej. 100+" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Países Recorridos</label>
                <input name="countriesCount" value={formData.countriesCount} onChange={handleChange} className="input-field" placeholder="Ej. 5" />
              </div>
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-purple-500/20 text-purple-400">
                <Globe size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Contacto & Redes</h2>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Email de Booking *</label>
                <input name="bookingEmail" value={formData.bookingEmail} onChange={handleChange} type="email" className="input-field" placeholder="booking@domain.com" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">WhatsApp de Booking</label>
                <input name="bookingWhatsapp" value={formData.bookingWhatsapp} onChange={handleChange} className="input-field" placeholder="+56 9 ..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Instagram *</label>
                <input name="instagram" value={formData.instagram} onChange={handleChange} className="input-field" placeholder="https://instagram.com/..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">SoundCloud Perfil *</label>
                <input name="soundcloudProfile" value={formData.soundcloudProfile} onChange={handleChange} className="input-field" placeholder="https://soundcloud.com/..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Spotify Artista</label>
                <input name="spotify" value={formData.spotify} onChange={handleChange} className="input-field" placeholder="https://open.spotify.com/..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">YouTube Canal</label>
                <input name="youtube" value={formData.youtube} onChange={handleChange} className="input-field" placeholder="https://youtube.com/..." />
              </div>
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-blue-500/20 text-blue-400">
                <User size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Biografía</h2>
            </div>
            <div className="grid grid-cols-1 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Bio en Español *</label>
                <textarea name="bioEs" value={formData.bioEs} onChange={handleChange} className="input-field min-h-[150px]" placeholder="Cuéntanos tu historia..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Bio en Inglés (Opcional)</label>
                <textarea name="bioEn" value={formData.bioEn} onChange={handleChange} className="input-field min-h-[150px]" placeholder="English version..."></textarea>
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Hitos Clave / Logros</label>
                <textarea name="highlights" value={formData.highlights} onChange={handleChange} className="input-field min-h-[100px]" placeholder="Festivales, sellos, premios, colaboraciones..." />
              </div>
            </div>
          </div>
        )}

        {step === 4 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-amber-500/20 text-amber-400">
                <Music size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Música & Video</h2>
            </div>
            <div className="grid grid-cols-1 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Sets de SoundCloud * (Un link por línea)</label>
                <textarea name="sets" value={formData.sets} onChange={handleChange} className="input-field min-h-[100px]" placeholder="https://soundcloud.com/..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Tracks/Releases SoundCloud * (Un link por línea)</label>
                <textarea name="releases" value={formData.releases} onChange={handleChange} className="input-field min-h-[100px]" placeholder="https://soundcloud.com/..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Video Principal (YouTube/Vimeo)</label>
                <input name="youtube" value={formData.youtube} onChange={handleChange} className="input-field" placeholder="https://youtube.com/..." />
              </div>
            </div>
          </div>
        )}

        {step === 5 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-pink-500/20 text-pink-400">
                <Camera size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Material Visual</h2>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Link Foto Hero *</label>
                <input name="heroPhotoLink" value={formData.heroPhotoLink} onChange={handleChange} className="input-field" placeholder="Google Drive / Dropbox" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Link Fotos Prensa *</label>
                <input name="pressPhotosLink" value={formData.pressPhotosLink} onChange={handleChange} className="input-field" placeholder="Google Drive / Dropbox" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Link Logo Negro *</label>
                <input name="logoBlackLink" value={formData.logoBlackLink} onChange={handleChange} className="input-field" placeholder="PNG Transparente" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Link Logo Color *</label>
                <input name="logoColorLink" value={formData.logoColorLink} onChange={handleChange} className="input-field" placeholder="PNG Transparente" />
              </div>
            </div>
          </div>
        )}

        {step === 6 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-cyan-500/20 text-cyan-400">
                <Calendar size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Agenda de Shows</h2>
            </div>
            <div className="flex flex-col gap-2">
              <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Shows Confirmados</label>
              <textarea 
                name="upcomingShows" 
                value={formData.upcomingShows} 
                onChange={handleChange} 
                className="input-field min-h-[200px]" 
                placeholder="Sigue este formato:&#10;YYYY-MM-DD | Lugar | Ciudad | País | estado (upcoming/sold-out) | link tickets" 
              />
            </div>
          </div>
        )}

        {step === 7 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-green-500/20 text-green-400">
                <Upload size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Press Kit & Extras</h2>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Link Carpeta Fotos HD</label>
                <input name="hdPhotosLink" value={formData.hdPhotosLink} onChange={handleChange} className="input-field" placeholder="Drive / Dropbox" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Link Rider Técnico</label>
                <input name="riderLink" value={formData.riderLink} onChange={handleChange} className="input-field" placeholder="PDF / Carpeta" />
              </div>
              <div className="flex flex-col gap-2 sm:col-span-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Otros Assets / Notas</label>
                <textarea name="extraAssets" value={formData.extraAssets} onChange={handleChange} className="input-field min-h-[100px]" placeholder="Videos, reels, referencias visuales..."></textarea>
              </div>
            </div>
          </div>
        )}

        {step === 8 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 rounded-2xl bg-white/10 text-white">
                <CheckCircle2 size={24} />
              </div>
              <h2 className="text-2xl font-black uppercase tracking-tight">Validación Final</h2>
            </div>
            <div className="grid grid-cols-1 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Fecha Límite deseada</label>
                <input name="deadline" value={formData.deadline} onChange={handleChange} type="date" className="input-field" />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Notas Adicionales</label>
                <textarea name="notes" value={formData.notes} onChange={handleChange} className="input-field min-h-[100px]" placeholder="Cualquier detalle extra..." />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Confirmación de Material *</label>
                <select name="confirmation" value={formData.confirmation} onChange={handleChange} className="input-field">
                  <option value="">Selecciona una opción</option>
                  <option value="ready">Confirmo que todo el material está completo y puede publicarse</option>
                  <option value="pending">Faltan algunos detalles que enviaré posteriormente</option>
                </select>
              </div>
            </div>
          </div>
        )}

        <div className="flex justify-between items-center mt-10 gap-4">
          <button 
            type="button" 
            onClick={prevStep} 
            disabled={step === 1}
            className="px-6 py-3 rounded-full text-sm font-bold uppercase tracking-widest text-slate-400 hover:text-white disabled:opacity-0 transition-all"
          >
            Anterior
          </button>
          
          {step < 8 ? (
            <button 
              type="button" 
              onClick={nextStep}
              disabled={!isStepValid()}
              className="btn-primary px-10 py-3 rounded-full text-sm font-black uppercase tracking-widest disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              Siguiente
            </button>
          ) : (
            <button 
              type="button" 
              onClick={async () => {
                onLoading(true);
                try {
                  await onSubmit(formData);
                } finally {
                  onLoading(false);
                }
              }}
              disabled={!formData.confirmation}
              className="btn-primary px-10 py-3 rounded-full text-sm font-black uppercase tracking-widest disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              Enviar Solicitud
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

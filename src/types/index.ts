export interface Profile {
  name: string;
  tagline: string;
  kicker: string;
  hero_lead: string;
  hero_badge: string;
  booking_email: string;
  promo_url: string;
  promo_copy: string;
  stat_city: string;
  stat_origin: string;
  stat_start: string;
}

export interface BioEntry {
  id?: number;
  language: string;
  content: string;
}

export interface Social {
  id?: number;
  platform: string;
  label: string;
  url: string;
  sort_order: number;
}

export interface MusicTrack {
  id?: number;
  title: string;
  type: string;
  embed_url: string;
  sort_order: number;
}

export interface Show {
  id?: number;
  date: string;
  venue: string;
  city: string;
  country: string;
  type: string;
  status: string;
  ticket_url: string;
}

export interface GalleryImage {
  id?: number;
  image_url: string;
  caption: string;
  sort_order: number;
}

export interface PageData {
  profile: Profile | null;
  bios: BioEntry[];
  socials: Social[];
  music: MusicTrack[];
  shows: Show[];
  gallery: GalleryImage[];
}

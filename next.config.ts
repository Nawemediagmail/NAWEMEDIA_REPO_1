import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [new URL("https://*.supabase.co/storage/v1/object/public/photos/**")],
  },
};

export default nextConfig;

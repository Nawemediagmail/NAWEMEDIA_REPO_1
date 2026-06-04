import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "NAWEMEDIA | Onboarding Portal",
  description: "Completá tu información para que NAWEMEDIA construya tu EPK profesional.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="h-full antialiased">
      <body className="min-h-full flex flex-col">{children}</body>
    </html>
  );
}

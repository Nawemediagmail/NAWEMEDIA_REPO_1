import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "SOFIFITVIBES DJ - Official EPK",
  description: "Official EPK for SOFIFITVIBES DJ. Afro House, Tribal and Circuit DJ based in Hamburg, Germany.",
  openGraph: {
    title: "SOFIFITVIBES DJ - Official EPK",
    description: "Afro House · Tribal · Circuit",
    type: "website",
  },
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

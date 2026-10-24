import { MetadataRoute } from "next";

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: "Aapan Gaon - आपका अपना ग्रामीण डिजिटल साथी",
    short_name: "Aapan Gaon",
    description: "ताज़ा मंडी भाव, ग्रामीण समाचार और गाँव की हर सुविधा।",
    start_url: "/",
    display: "standalone",
    background_color: "#ffffff",
    theme_color: "#166534",
    icons: [
      {
        src: "/logo.png",
        sizes: "192x192",
        type: "image/png",
      },
      {
        src: "/logo.png",
        sizes: "512x512",
        type: "image/png",
      },
    ],
  };
}

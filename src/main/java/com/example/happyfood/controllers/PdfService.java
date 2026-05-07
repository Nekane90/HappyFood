package com.example.happyfood.controllers;

import com.example.happyfood.controllers.TraductorService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class PdfService{

    private final Color colorMenta = new Color(225, 240, 237); // #E1F0ED
    private final Color verdeOscuro = new Color(27, 67, 50);  // #1B4332

    public void generarPdfDesdeJson(String nombrePlan, String jsonContent) {
        // Añadimos márgenes al documento: Izquierda, Derecha, Arriba, Abajo
        Document documento = new Document(PageSize.A4, 50, 50, 60, 50);

        try {
            String ruta = System.getProperty("user.home") + "/Downloads/" + nombrePlan.replaceAll(" ", "_") + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(ruta));

            //pintar el fondo en todas las paginas
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    Rectangle rect = writer.getPageSize();
                    canvas.setColorFill(colorMenta);
                    canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
                    canvas.fill();
                }
            });

            documento.open();

            // --- FUENTES ---
            Font fuenteTituloApp = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, verdeOscuro);
            Font fuenteNombrePlan = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.DARK_GRAY);
            Font fuenteDia = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, verdeOscuro);
            Font fuentePlato = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

            try {
                String rutaLogo = getClass().getResource("/imagenes/logo.png").toExternalForm();
                Image imgLogo = Image.getInstance(rutaLogo);
                imgLogo.scaleToFit(100, 100);
                // Bajamos el logo (antes 750, ahora 730 para dar más margen superior)
                imgLogo.setAbsolutePosition(470f, 730f);
                documento.add(imgLogo);
            } catch (Exception e) {
                System.err.println("⚠️ Logo no encontrado.");
            }

            // 1. Título de la App centrado
            Paragraph titulo = new Paragraph("Happy Food", fuenteTituloApp);
            titulo.setAlignment(Element.ALIGN_CENTER); // <-- Centra el texto
            titulo.setSpacingAfter(10);
            documento.add(titulo);

            // 2. Nombre de la planificación centrado
            Paragraph subPlan = new Paragraph("Planificación: " + nombrePlan, fuenteNombrePlan);
            subPlan.setAlignment(Element.ALIGN_CENTER); // <-- Centra el texto
            documento.add(subPlan);

            // 3. Fecha de generación centrada
            Paragraph fecha = new Paragraph("Generado el: " + java.time.LocalDate.now(), fuenteNombrePlan);
            fecha.setAlignment(Element.ALIGN_CENTER); // <-- Centra el texto
            fecha.setSpacingAfter(30);
            documento.add(fecha);

            // --- PROCESO DEL JSON ---
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            JsonObject week = jsonObject.getAsJsonObject("week");

            for (Map.Entry<String, JsonElement> entry : week.entrySet()) {
                String diaTraducido = TraductorService.traducirFrase(entry.getKey()).toUpperCase();

                Paragraph pDia = new Paragraph("--------- " + diaTraducido + " ---------", fuenteDia);
                pDia.setSpacingBefore(20);
                pDia.setSpacingAfter(15);
                documento.add(pDia);

                JsonObject datosDia = entry.getValue().getAsJsonObject();
                JsonArray meals = datosDia.getAsJsonArray("meals");

                for (JsonElement mealElem : meals) {
                    JsonObject meal = mealElem.getAsJsonObject();
                    String tituloTraducido = TraductorService.traducirFrase(meal.get("title").getAsString());

                    Paragraph pPlato = new Paragraph("• " + tituloTraducido, fuentePlato);
                    pPlato.setSpacingBefore(5);
                    documento.add(pPlato);

                }
            }
            Font fuentePie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 18, verdeOscuro);
            Paragraph pieFinal = new Paragraph("¡Gracias por usar nuestra App!", fuentePie);
            pieFinal.setAlignment(Element.ALIGN_CENTER);

            // Esto lo añade al final del contenido en la última página
            pieFinal.setSpacingBefore(50f);
            documento.add(pieFinal);

            documento.close();

            // Abrir automáticamente
            File file = new File(ruta);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


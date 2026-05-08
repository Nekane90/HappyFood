package com.example.happyfood.controllers;

import java.io.IOException;

public class Lector {
    private static Process procesoVoz;

    public static void leerEnVozAlta(String texto) {
        // Detenemos lo anterior sin usar taskkill (que es muy agresivo)
        detenerVoz();

        new Thread(() -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    // 1. Limpieza extrema del texto para evitar errores de sintaxis en PowerShell
                    String textoLimpio = texto.replace("'", "").replace("\"", "").replace("\n", " ");

                    // 2. Comando simplificado: Add-Type carga la librería de voz de Windows
                    String comando = "Add-Type -AssemblyName System.Speech; " +
                            "$sim = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                            "$sim.Speak('" + textoLimpio + "')";

                    ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", comando);
                    procesoVoz = pb.start();

                } else if (os.contains("mac")) {
                    procesoVoz = new ProcessBuilder("say", texto).start();
                }
            } catch (IOException e) {
                System.err.println("Error al iniciar voz: " + e.getMessage());
            }
        }).start();
    }

    public static void detenerVoz() {
        if (procesoVoz != null && procesoVoz.isAlive()) {
            procesoVoz.destroyForcibly();
            // Esperamos un instante para que el sistema libere el recurso
        }
    }
}
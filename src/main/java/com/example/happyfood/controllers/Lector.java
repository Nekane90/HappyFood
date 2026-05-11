package com.example.happyfood.controllers;

import java.io.IOException;

public class Lector {

    private static Process procesoVoz;

    public static void leerEnVozAlta(String texto) {
        detenerVoz();
        new Thread(() -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                if(os.contains("win")) {

                    String textoLimpio = texto.replace("'", "").replace("\"", "").replace("\n", " ");
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
        }
    }

}
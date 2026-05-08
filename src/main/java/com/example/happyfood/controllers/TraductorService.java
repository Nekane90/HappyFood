package com.example.happyfood.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TraductorService {

    public static String traducirFrase(String texto) {
        if (texto == null || texto.isEmpty()) return texto;

        try {
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=es&dt=t&q="
                    + URLEncoder.encode(texto, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // --- AQUÍ EMPIEZA EL BLOQUE NUEVO ---
                JsonArray json = JsonParser.parseString(response.body()).getAsJsonArray();

                // Google devuelve un array. El primer elemento (índice 0) tiene los fragmentos.
                JsonArray partes = json.get(0).getAsJsonArray();
                StringBuilder resultadoCompleto = new StringBuilder();

                // Recorremos todos los fragmentos para juntarlos todos
                for (int i = 0; i < partes.size(); i++) {
                    JsonArray fragmento = partes.get(i).getAsJsonArray();
                    // El texto traducido es siempre el primer elemento del fragmento
                    if (!fragmento.get(0).isJsonNull()) {
                        resultadoCompleto.append(fragmento.get(0).getAsString());
                    }
                }
                return resultadoCompleto.toString().trim();
                // --- AQUÍ TERMINA EL BLOQUE NUEVO ---

            } else {
                System.err.println("Error Google: " + response.statusCode());
                return texto;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return texto;
        }
    }
    public static String traducirAIngles(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        try {
            // Cambiamos sl=es (español) y tl=en (inglés)
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=en&dt=t&q="
                    + URLEncoder.encode(texto, StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "Mozilla/5.0").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray json = JsonParser.parseString(response.body()).getAsJsonArray();
                JsonArray partes = json.get(0).getAsJsonArray();
                StringBuilder resultado = new StringBuilder();
                for (int i = 0; i < partes.size(); i++) {
                    resultado.append(partes.get(i).getAsJsonArray().get(0).getAsString());
                }
                return resultado.toString().trim();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return texto;
    }
}


package com.example.happyfood.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiController {
    private static final String API_KEY = "e12ee1226aa1476f96f3497e8bad1a8e";

    // URL para generar un plan de comidas semanal
    private static final String URL_PLAN_SEMANAL = "https://api.spoonacular.com/mealplanner/generate?timeFrame=week";

    public String obtenerPlanSemanal(String dieta, String intolerancias) throws Exception {
        StringBuilder urlFinal = new StringBuilder(URL_PLAN_SEMANAL);
        urlFinal.append("&apiKey=").append(API_KEY);

        if (dieta != null && !dieta.isEmpty()) {
            urlFinal.append("&diet=").append(dieta);
        }

        // Aquí recibimos lo que viene de la BBDD (que ya está en inglés)
        if (intolerancias != null && !intolerancias.isEmpty() && !intolerancias.equalsIgnoreCase("null")) {
            urlFinal.append("&exclude=").append(intolerancias.replace(" ", ""));
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFinal.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("URL ENVIADA A API: " + urlFinal.toString());

        return response.body();

    }

    // Mantienes tu método de buscar recetas individuales si lo usas en otra parte
    public String buscarRecetas(String ingrediente) throws Exception {
        String urlFinal = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + API_KEY + "&query=" + ingrediente;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlFinal)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String obtenerDetallesReceta(int id) throws Exception {
        String url = "https://api.spoonacular.com/recipes/" + id + "/information?apiKey=" + API_KEY;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }


}

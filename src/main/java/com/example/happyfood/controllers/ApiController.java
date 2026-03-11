package com.example.happyfood.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiController {
    private static final String API_KEY = "e12ee1226aa1476f96f3497e8bad1a8e";

    // URL para generar un plan de comidas semanal
    private static final String URL_PLAN_SEMANAL = "https://api.spoonacular.com/mealplanner/generate?timeFrame=week";

    public String obtenerPlanSemanal() throws Exception {
        // Construimos la URL con la API KEY
        String urlFinal = URL_PLAN_SEMANAL + "&apiKey=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFinal))
                .GET() // Es una petición de lectura
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Error en la API: Código " + response.statusCode());
        }

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

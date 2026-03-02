package com.example.happyfood.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiController {
    private static final String API_KEY = "e12ee1226aa1476f96f3497e8bad1a8e";
    private static final String BASE_URL = "https://api.spoonacular.com/recipes/complexSearch";

    // Hay que comprobar si conecta bien con la api///
    /// ///
    /// /////
    /// /////
    public String buscarRecetas(String ingrediente) throws Exception {
        // 1. Construimos la URL con el filtro y la llave
        String urlFinal = BASE_URL + "?apiKey=" + API_KEY + "&query=" + ingrediente;

        // 2. Creamos el cliente y la petición
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFinal))
                .build();

        // 3. Enviamos y recibimos la respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body(); // Esto devuelve el JSON con las recetas
    }
}


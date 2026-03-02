package com.example.happyfood.controllers;

import happyDTO.Usuario;

/// esta clase es la que trae del login el usuario logeado
public class Sesion {

    private static Usuario usuarioLogueado;

    public static void setUsuario(Usuario u) {
        usuarioLogueado = u;
    }

    public static Usuario getUsuario() {
        return usuarioLogueado;
    }
}

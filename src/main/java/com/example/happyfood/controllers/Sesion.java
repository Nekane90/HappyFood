package com.example.happyfood.controllers;

import happyDTO.UsuarioDto;

/// esta clase es la que trae del login el usuario logeado
public class Sesion {

    private static UsuarioDto usuarioLogueado;

    public static void setUsuario(UsuarioDto u) {
        usuarioLogueado = u;
    }

    public static UsuarioDto getUsuario() {
        return usuarioLogueado;
    }
}

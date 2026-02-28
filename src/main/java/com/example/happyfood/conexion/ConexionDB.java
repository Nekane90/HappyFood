package com.example.happyfood.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConexionDB {
    private static final String URL = "jdbc:postgresql://ep-morning-cherry-aliz58fv-pooler.c-3.eu-central-1.aws.neon.tech/neondb";

    public static Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");

            Properties props = new Properties();
            props.setProperty("user", "neondb_owner");
            props.setProperty("password", "npg_KGkqbxo78mMv");
            props.setProperty("sslmode", "require");
            props.setProperty("channel_binding", "require");
            props.setProperty("connectTimeout", "10");

            Connection conexion = DriverManager.getConnection(URL, props);
            if (conexion != null) {
                System.out.println("✅ ¡CONEXIÓN ESTABLECIDA!");
            }
            return conexion;
        } catch (Exception e) {
            System.err.println("❌ ERROR AL CONECTAR:");
            System.err.println("Causa: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getName());
            return null;
        }
    }
}
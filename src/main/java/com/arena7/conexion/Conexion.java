package com.arena7.conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/cancha_deportiva?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    public static Connection conectar() {
        Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("CONEXION EXITOSA A CANCHA_DEPORTIVA");
        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION: " + e.getMessage());
        }

        return conexion;
    }
}
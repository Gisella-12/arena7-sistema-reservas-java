package com.arena7.conexion;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        Connection cn = Conexion.conectar();

        if (cn != null) {
            System.out.println("BASE DE DATOS CONECTADA CORRECTAMENTE");
        } else {
            System.out.println("NO SE PUDO CONECTAR");
        }
    }
}
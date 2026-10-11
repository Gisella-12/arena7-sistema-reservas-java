package com.arena7.dao;

import com.arena7.conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean validar(String correo, String clave) throws SQLException {
        String sql = "SELECT id FROM usuarios WHERE correo = ? AND password = ?";

        try (Connection cn = Conexion.conectar()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar a MySQL.");
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, correo);
                ps.setString(2, clave);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }
}
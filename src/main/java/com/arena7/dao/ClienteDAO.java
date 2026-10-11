package com.arena7.dao;

import com.arena7.conexion.Conexion;
import com.arena7.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> listar() throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
                SELECT id, dni, nombre, telefono
                FROM clientes
                ORDER BY nombre, id
                """;

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();

                cliente.setId(rs.getInt("id"));
                cliente.setDni(rs.getString("dni"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setTelefono(rs.getString("telefono"));

                clientes.add(cliente);
            }
        }

        return clientes;
    }

    private Connection conectar() throws SQLException {

        Connection cn = Conexion.conectar();

        if (cn == null) {
            throw new SQLException(
                    "No se pudo conectar con la base de datos.");
        }

        return cn;
    }
}
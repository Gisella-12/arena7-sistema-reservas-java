package com.arena7.servlet;

import com.arena7.conexion.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(
    name = "GuardarReservaServlet",
    urlPatterns = {"/guardar_reserva"}
)
public class GuardarReservaServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        String dni = request.getParameter("dni");
        String cliente = request.getParameter("cliente");
        String telefono = request.getParameter("telefono");
        String cancha = request.getParameter("cancha");
        String fecha = request.getParameter("fecha");
        String hora = request.getParameter("hora");
        String pago = request.getParameter("pago");
        String codigoPago = request.getParameter("codigoPago");
        String monto = request.getParameter("monto");
        String detalle = request.getParameter("detalle");
        String usuario = request.getParameter("usuario");

        if (dni == null || dni.trim().isEmpty() || cliente == null || cliente.trim().isEmpty()) {
            response.getWriter().print("Error: Faltan datos obligatorios.");
            return;
        }

        // 1. Consulta para registrar o actualizar el cliente (evita errores de FK)
        String sqlCliente = """
            INSERT INTO clientes (dni, nombre, telefono) 
            VALUES (?, ?, ?) 
            ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), telefono = VALUES(telefono)
            """;

        // 2. Consulta para registrar la reserva
        String sqlReserva = """
            INSERT INTO reservas
            (dni, cliente, telefono, cancha, fecha, hora, pago, codigo_pago, monto, detalle, usuario, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Confirmado')
            """;

        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false); // Transacción segura

            try (
                PreparedStatement psCliente = cn.prepareStatement(sqlCliente);
                PreparedStatement psReserva = cn.prepareStatement(sqlReserva)
            ) {
                // Registrar o asegurar el cliente primero
                psCliente.setString(1, dni);
                psCliente.setString(2, cliente);
                psCliente.setString(3, telefono);
                psCliente.executeUpdate();

                // Registrar la reserva después
                psReserva.setString(1, dni);
                psReserva.setString(2, cliente);
                psReserva.setString(3, telefono);
                psReserva.setString(4, cancha);
                psReserva.setString(5, fecha);
                psReserva.setString(6, hora);
                psReserva.setString(7, pago);
                psReserva.setString(8, codigoPago);
                psReserva.setString(9, monto);
                psReserva.setString(10, detalle);
                psReserva.setString(11, usuario);
                
                psReserva.executeUpdate();

                cn.commit(); // Confirmar cambios en ambas tablas
                response.getWriter().print("ok");

            } catch (Exception e) {
                cn.rollback(); // Revertir todo si algo falla
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Error en MySQL: " + e.getMessage());
        }
    }
}
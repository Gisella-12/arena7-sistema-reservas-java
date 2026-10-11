package com.arena7.servlet;

import com.arena7.dao.ReservaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

@WebServlet(
        name = "ReporteReservasServlet",
        urlPatterns = {"/reporte-reservas"}
)
public class ReporteReservasServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession sesion = request.getSession(false);
        Object usuario = sesion == null
                ? null : sesion.getAttribute("usuario");

        // Solo permite consultar el reporte con sesión iniciada.
        if (!(usuario instanceof String correo) || correo.isBlank()) {
            response.sendRedirect(
                    request.getContextPath() + "/login.xhtml");
            return;
        }

        try {
            ReservaDAO dao = new ReservaDAO();

            // Envía los datos de MySQL a la página JSP.
            request.setAttribute("reservas", dao.listar());

        } catch (SQLException e) {
            getServletContext().log(
                    "Error al consultar el reporte de reservas", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            request.setAttribute(
                    "reservas", Collections.emptyList());

            request.setAttribute(
                    "error",
                    "No se pudieron cargar las reservas de MySQL.");
        }

        // La JSP se encarga de mostrar los datos.
        request.getRequestDispatcher(
                "/WEB-INF/vistas/reporte-reservas.jsp")
                .forward(request, response);
    }
}
package com.arena7.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {
    "/dashboard.xhtml",
    "/clientes.xhtml",
    "/reservas.xhtml",
    "/nueva-reserva.xhtml",
    "/calendario.xhtml",
    "/guardar_reserva"
})
public class FiltroSesion implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest peticion =
                (HttpServletRequest) request;

        HttpServletResponse respuesta =
                (HttpServletResponse) response;

        respuesta.setHeader(
                "Cache-Control", "no-store, no-cache, must-revalidate");
        respuesta.setHeader("Pragma", "no-cache");
        respuesta.setDateHeader("Expires", 0);

        HttpSession sesion = peticion.getSession(false);

        Object usuario = sesion == null
                ? null : sesion.getAttribute("usuario");

        if (usuario instanceof String correo && !correo.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        String login = peticion.getContextPath() + "/login.xhtml";

        if ("partial/ajax".equals(
                peticion.getHeader("Faces-Request"))) {

            respuesta.setContentType("text/xml;charset=UTF-8");
            respuesta.getWriter().write(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<partial-response><redirect url=\""
                    + login
                    + "\"/></partial-response>");

        } else {
            respuesta.sendRedirect(login);
        }
    }
}
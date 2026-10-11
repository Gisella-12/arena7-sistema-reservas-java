package com.arena7.servlet;

import com.arena7.dao.ReservaDAO;
import com.arena7.modelo.Reserva;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Set;

@WebServlet(
        name = "GuardarReservaServlet",
        urlPatterns = {"/guardar_reserva"}
)
public class GuardarReservaServlet extends HttpServlet {

    private static final Set<String> HORAS = Set.of(
            "08", "09", "10", "11", "12", "13", "14",
            "15", "16", "18", "19", "20", "21"
    );

    private static final Set<String> PAGOS =
            Set.of("Yape", "Plin", "Efectivo");

    private String dato(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        HttpSession sesion = request.getSession(false);
        Object usuario = sesion == null
                ? null : sesion.getAttribute("usuario");

        if (!(usuario instanceof String correo) || correo.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(
                    "Inicia sesión para guardar una reserva.");
            return;
        }

        try {
            Reserva reserva = new Reserva();

            reserva.setDni(dato(request, "dni"));
            reserva.setCliente(dato(request, "cliente"));
            reserva.setTelefono(dato(request, "telefono"));
            reserva.setCancha(
                    Integer.parseInt(dato(request, "cancha")));
            reserva.setFecha(
                    LocalDate.parse(dato(request, "fecha")));
            reserva.setHora(dato(request, "hora"));
            reserva.setPago(dato(request, "pago"));
            reserva.setCodigoPago(dato(request, "codigoPago"));
            reserva.setMonto(
                    new BigDecimal(dato(request, "monto")));
            reserva.setDetalle(dato(request, "detalle"));

            // El usuario se obtiene de la sesión iniciada.
            reserva.setUsuario(correo);

            if (!reserva.getDni().matches("[0-9]{8}")
                    || reserva.getCliente().isBlank()
                    || reserva.getCliente().length() > 100
                    || reserva.getTelefono().isBlank()
                    || reserva.getTelefono().length() > 15
                    || reserva.getCancha() < 1
                    || reserva.getCancha() > 4
                    || !HORAS.contains(reserva.getHora())
                    || !PAGOS.contains(reserva.getPago())
                    || reserva.getCodigoPago().length() > 50
                    || reserva.getMonto().signum() <= 0
                    || reserva.getMonto().compareTo(
                            new BigDecimal("99999999.99")) > 0) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(
                        "Revisa los datos del formulario.");
                return;
            }

            new ReservaDAO().guardar(reserva);

            response.getWriter().print("ok");

        } catch (SQLException e) {
            getServletContext().log(
                    "Error al guardar la reserva", e);

            boolean horarioOcupado = false;

            for (SQLException error = e;
                    error != null;
                    error = error.getNextException()) {

                if (error.getErrorCode() == 1062
                        && error.getMessage() != null
                        && error.getMessage()
                                .contains("uq_reserva_activa")) {

                    horarioOcupado = true;
                    break;
                }
            }

            if (horarioOcupado) {
                response.setStatus(
                        HttpServletResponse.SC_CONFLICT);
                response.getWriter().print(
                        "Este horario ya está reservado. "
                        + "Elige otra hora, fecha o cancha.");
            } else {
                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(
                        "No se pudo guardar la reserva.");
            }

        } catch (IllegalArgumentException | DateTimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "Revisa los datos del formulario.");
        }
    }
}
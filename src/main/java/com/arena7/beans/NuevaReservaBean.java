package com.arena7.beans;

import com.arena7.dao.ReservaDAO;
import com.arena7.modelo.Reserva;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Set;

@Named("nuevaReservaBean")
@ViewScoped
public class NuevaReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Reserva reserva = new Reserva();
    private boolean edicion;

    public String cargarParaEditar() {
        FacesContext contexto = FacesContext.getCurrentInstance();

        if (correoSesion() == null) {
            return "login?faces-redirect=true";
        }

        if (id == null) {
            return null;
        }

        try {
            Reserva encontrada = new ReservaDAO().buscarPorId(id);

            if (encontrada == null || encontrada.isCancelada()) {
                mensaje(FacesMessage.SEVERITY_WARN,
                        "La reserva no existe o está cancelada.");

                contexto.getExternalContext()
                        .getFlash().setKeepMessages(true);

                return "calendario?faces-redirect=true";
            }

            reserva = encontrada;
            edicion = true;

        } catch (SQLException e) {
            contexto.getExternalContext().log(
                    "Error al consultar la reserva", e);

            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se pudo abrir la reserva.");

            contexto.getExternalContext()
                    .getFlash().setKeepMessages(true);

            return "calendario?faces-redirect=true";
        }

        return null;
    }

    public String guardar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        String correo = correoSesion();

        if (correo == null) {
            return "login?faces-redirect=true";
        }

        Set<String> horas = Set.of(
                "08", "09", "10", "11", "12", "13", "14",
                "15", "16", "18", "19", "20", "21"
        );

        Set<String> pagos = Set.of("Yape", "Plin", "Efectivo");

        if (reserva.getDni() == null
                || !reserva.getDni().matches("[0-9]{8}")
                || reserva.getCliente() == null
                || reserva.getCliente().isBlank()
                || reserva.getCliente().length() > 100
                || reserva.getTelefono() == null
                || reserva.getTelefono().isBlank()
                || reserva.getTelefono().length() > 15
                || reserva.getCancha() < 1
                || reserva.getCancha() > 4
                || reserva.getFecha() == null
                || reserva.getHora() == null
                || !horas.contains(reserva.getHora())
                || reserva.getPago() == null
                || !pagos.contains(reserva.getPago())
                || reserva.getMonto() == null
                || reserva.getMonto().signum() <= 0
                || reserva.getMonto().compareTo(
                        new BigDecimal("99999999.99")) > 0
                || (reserva.getCodigoPago() != null
                    && reserva.getCodigoPago().length() > 50)) {

            mensaje(FacesMessage.SEVERITY_ERROR,
                    "Revisa los datos del cliente, la cancha, "
                            + "el horario, el pago y el monto.");

            return null;
        }

        reserva.setCliente(reserva.getCliente().trim());
        reserva.setTelefono(reserva.getTelefono().trim());

        try {
            ReservaDAO dao = new ReservaDAO();

            if (edicion) {
                if (id == null || id.intValue() != reserva.getId()) {
                    mensaje(FacesMessage.SEVERITY_ERROR,
                            "No se pudo identificar la reserva.");
                    return null;
                }

                dao.actualizar(reserva);

                mensaje(FacesMessage.SEVERITY_INFO,
                        "Reserva actualizada correctamente.");

            } else {
                reserva.setUsuario(correo);
                dao.guardar(reserva);

                mensaje(FacesMessage.SEVERITY_INFO,
                        "Reserva guardada correctamente.");
            }

            contexto.getExternalContext()
                    .getFlash().setKeepMessages(true);

            return "calendario?faces-redirect=true";

        } catch (SQLException e) {
            contexto.getExternalContext().log(
                    "Error al guardar la reserva", e);

            boolean horarioOcupado = false;

            for (SQLException error = e;
                    error != null;
                    error = error.getNextException()) {

                if (error.getErrorCode() == 1062
                        && error.getMessage() != null
                        && error.getMessage().contains("uq_reserva_activa")) {

                    horarioOcupado = true;
                    break;
                }
            }

            if (horarioOcupado) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "Este horario ya está reservado en la cancha "
                                + "seleccionada. Elige otra hora, fecha o cancha.");
            } else {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "No se pudieron guardar los cambios. "
                                + "Revisa la conexión o si la reserva fue cancelada.");
            }

            return null;
        }
    }

    private String correoSesion() {
        Object usuario = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuario");

        if (usuario instanceof String correo && !correo.isBlank()) {
            return correo;
        }

        return null;
    }

    private void mensaje(FacesMessage.Severity gravedad, String texto) {
        FacesContext.getCurrentInstance().addMessage(
                null, new FacesMessage(gravedad, texto, null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isEdicion() {
        return edicion;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}
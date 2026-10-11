package com.arena7.beans;

import com.arena7.dao.ReservaDAO;
import com.arena7.modelo.Reserva;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Reserva> reservas = new ArrayList<>();
    private String buscarCliente = "";
    private LocalDate buscarFecha;
    private String buscarEstado = "";
    private boolean errorCarga;

    @PostConstruct
    public void cargar() {
        try {
            reservas = new ReservaDAO().listar();
            errorCarga = false;

        } catch (SQLException e) {
            reservas = new ArrayList<>();
            errorCarga = true;

            FacesContext contexto = FacesContext.getCurrentInstance();

            contexto.getExternalContext().log(
                    "Error al cargar las reservas", e);

            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se pudieron cargar las reservas de MySQL.");
        }
    }

    public String cancelar(int id) {
        FacesContext contexto = FacesContext.getCurrentInstance();

        Object usuario = contexto.getExternalContext()
                .getSessionMap().get("usuario");

        if (!(usuario instanceof String correo) || correo.isBlank()) {
            contexto.getExternalContext().invalidateSession();
            return "login?faces-redirect=true";
        }

        try {
            boolean cambio = new ReservaDAO().cancelar(id);

            cargar();

            if (cambio) {
                mensaje(FacesMessage.SEVERITY_INFO,
                        "Reserva cancelada correctamente.");
            } else {
                mensaje(FacesMessage.SEVERITY_WARN,
                        "La reserva ya estaba cancelada o no existe.");
            }

        } catch (SQLException e) {
            contexto.getExternalContext().log(
                    "Error al cancelar la reserva", e);

            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se pudo cancelar la reserva.");
        }

        return null;
    }

    private void mensaje(FacesMessage.Severity gravedad, String texto) {
        FacesContext.getCurrentInstance().addMessage(
                null, new FacesMessage(gravedad, texto, null));
    }

    public void buscar() {
        cargar();
    }

    public void limpiar() {
        buscarCliente = "";
        buscarFecha = null;
        buscarEstado = "";
        cargar();
    }

    public List<Reserva> getReservasFiltradas() {
        String texto = buscarCliente == null
                ? "" : buscarCliente.trim().toLowerCase(Locale.ROOT);

        return reservas.stream()
                .filter(r -> texto.isEmpty()
                        || (r.getCliente() != null
                            && r.getCliente().toLowerCase(Locale.ROOT)
                                    .contains(texto))
                        || (r.getDni() != null
                            && r.getDni().contains(texto)))
                .filter(r -> buscarFecha == null
                        || buscarFecha.equals(r.getFecha()))
                .filter(r -> buscarEstado == null
                        || buscarEstado.isEmpty()
                        || buscarEstado.equalsIgnoreCase(r.getEstado()))
                .toList();
    }

    public long getReservasDelDia() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));

        return reservas.stream()
                .filter(r -> hoy.equals(r.getFecha()))
                .filter(r -> !r.isCancelada())
                .count();
    }

    public long getConfirmadas() {
        return reservas.stream()
                .filter(r -> "Confirmado".equalsIgnoreCase(r.getEstado())
                        || "Confirmada".equalsIgnoreCase(r.getEstado()))
                .count();
    }

    public long getPendientes() {
        return reservas.stream()
                .filter(r -> "Pendiente".equalsIgnoreCase(r.getEstado()))
                .count();
    }

    public String getBuscarCliente() {
        return buscarCliente;
    }

    public void setBuscarCliente(String buscarCliente) {
        this.buscarCliente = buscarCliente;
    }

    public LocalDate getBuscarFecha() {
        return buscarFecha;
    }

    public void setBuscarFecha(LocalDate buscarFecha) {
        this.buscarFecha = buscarFecha;
    }

    public String getBuscarEstado() {
        return buscarEstado;
    }

    public void setBuscarEstado(String buscarEstado) {
        this.buscarEstado = buscarEstado;
    }

    public boolean isErrorCarga() {
        return errorCarga;
    }
}
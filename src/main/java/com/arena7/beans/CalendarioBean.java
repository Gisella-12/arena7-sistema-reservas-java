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

@Named("calendarioBean")
@ViewScoped
public class CalendarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate fecha =
            LocalDate.now(ZoneId.of("America/Lima"));

    private String turno = "noche";
    private List<Reserva> reservas = new ArrayList<>();
    private boolean errorCarga;

    @PostConstruct
    public void consultar() {
        try {
            reservas = new ReservaDAO().listar();
            errorCarga = false;

        } catch (SQLException e) {
            reservas = new ArrayList<>();
            errorCarga = true;

            FacesContext contexto = FacesContext.getCurrentInstance();

            contexto.getExternalContext().log(
                    "Error al consultar la disponibilidad", e);

            contexto.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo consultar la disponibilidad de las canchas.",
                    null
            ));
        }
    }

    public List<Reserva> getHorarios() {
        List<String> horas;

        if ("manana".equals(turno)) {
            horas = List.of("08", "09", "10", "11");

        } else if ("tarde".equals(turno)) {
            horas = List.of("12", "13", "14", "15", "16");

        } else {
            horas = List.of("18", "19", "20", "21");
        }

        List<Reserva> horarios = new ArrayList<>();

        for (String hora : horas) {
            Reserva horario = new Reserva();
            horario.setHora(hora);
            horarios.add(horario);
        }

        return horarios;
    }

    public boolean ocupado(int cancha, String hora) {
        // Si falla la consulta, no mostrar horarios como disponibles.
        if (errorCarga || fecha == null) {
            return true;
        }

        return reservas.stream()
                .anyMatch(r -> !r.isCancelada()
                        && r.getCancha() == cancha
                        && fecha.equals(r.getFecha())
                        && mismaHora(r.getHora(), hora));
    }

    private boolean mismaHora(String primera, String segunda) {
        if (primera == null || segunda == null) {
            return false;
        }

        try {
            return Integer.parseInt(primera.trim())
                    == Integer.parseInt(segunda.trim());

        } catch (NumberFormatException e) {
            return primera.equals(segunda);
        }
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public boolean isErrorCarga() {
        return errorCarga;
    }
}
package com.arena7.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String dni;
    private String cliente;
    private String telefono;
    private int cancha;
    private LocalDate fecha;
    private String hora;
    private String pago;
    private String codigoPago;
    private BigDecimal monto;
    private String detalle;
    private String usuario;
    private String estado;

    public String getHoraFormateada() {
        if (hora == null || hora.isBlank()) {
            return "";
        }

        try {
            int numero = Integer.parseInt(hora.trim());

            if (numero < 0 || numero > 23) {
                return hora;
            }

            int hora12 = numero % 12;
            if (hora12 == 0) {
                hora12 = 12;
            }

            return hora12 + ":00 "
                    + (numero < 12 ? "a. m." : "p. m.");

        } catch (NumberFormatException e) {
            return hora;
        }
    }

    public boolean isCancelada() {
        return "Cancelado".equalsIgnoreCase(estado)
                || "Cancelada".equalsIgnoreCase(estado);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getCancha() {
        return cancha;
    }

    public void setCancha(int cancha) {
        this.cancha = cancha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public String getCodigoPago() {
        return codigoPago;
    }

    public void setCodigoPago(String codigoPago) {
        this.codigoPago = codigoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
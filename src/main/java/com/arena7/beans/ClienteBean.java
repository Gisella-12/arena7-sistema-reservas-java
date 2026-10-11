package com.arena7.beans;

import com.arena7.dao.ClienteDAO;
import com.arena7.modelo.Cliente;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("clienteBean")
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Cliente> clientes = new ArrayList<>();
    private String buscarCliente = "";
    private boolean errorCarga;

    @PostConstruct
    public void cargar() {
        try {
            clientes = new ClienteDAO().listar();
            errorCarga = false;

        } catch (SQLException e) {
            clientes = new ArrayList<>();
            errorCarga = true;

            FacesContext contexto = FacesContext.getCurrentInstance();

            contexto.getExternalContext().log(
                    "Error al consultar los clientes", e);

            contexto.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudieron cargar los clientes de MySQL.",
                    null));
        }
    }

    public void buscar() {
        cargar();
    }

    public void limpiar() {
        buscarCliente = "";
        cargar();
    }

    public List<Cliente> getClientesFiltrados() {
        String texto = buscarCliente == null
                ? ""
                : buscarCliente.trim().toLowerCase(Locale.ROOT);

        return clientes.stream()
                .filter(c -> texto.isEmpty()
                        || (c.getNombre() != null
                            && c.getNombre()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(texto))
                        || (c.getDni() != null
                            && c.getDni().contains(texto)))
                .toList();
    }

    public int getTotalClientes() {
        return clientes.size();
    }

    public String getBuscarCliente() {
        return buscarCliente;
    }

    public void setBuscarCliente(String buscarCliente) {
        this.buscarCliente = buscarCliente;
    }

    public boolean isErrorCarga() {
        return errorCarga;
    }
}
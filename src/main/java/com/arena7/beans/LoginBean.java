package com.arena7.beans;

import com.arena7.dao.UsuarioDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.SQLException;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String correo;
    private String clave;
    private boolean autenticado;

    public String ingresar() {
        FacesContext contexto = FacesContext.getCurrentInstance();

        autenticado = false;
        contexto.getExternalContext().getSessionMap().remove("usuario");
        contexto.getExternalContext().getSessionMap().remove("clave");

        try {
            if (correo == null || correo.isBlank()
                    || clave == null || clave.isBlank()) {

                mensaje(contexto, "Ingresa tu correo y contraseña.");
                return null;
            }

            correo = correo.trim();

            if (new UsuarioDAO().validar(correo, clave)) {

                HttpServletRequest request =
                        (HttpServletRequest) contexto
                                .getExternalContext().getRequest();

                contexto.getExternalContext().getSession(true);
                request.changeSessionId();

                contexto.getExternalContext()
                        .getSessionMap().put("usuario", correo);

                autenticado = true;

                return "dashboard?faces-redirect=true";
            }

            mensaje(contexto, "Correo o contraseña incorrectos.");

        } catch (SQLException e) {
            contexto.getExternalContext().log(
                    "Error al validar el login", e);

            mensaje(contexto,
                    "No se pudo conectar con la base de datos.");

        } finally {
            clave = null;
        }

        return null;
    }

    public String salir() {
        autenticado = false;
        correo = null;
        clave = null;

        FacesContext.getCurrentInstance()
                .getExternalContext().invalidateSession();

        return "login?faces-redirect=true";
    }

    private void mensaje(FacesContext contexto, String texto) {
        contexto.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, texto, null));
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean isAutenticado() {
        return autenticado;
    }
}
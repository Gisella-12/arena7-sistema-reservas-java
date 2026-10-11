package com.arena7.dao;

import com.arena7.conexion.Conexion;
import com.arena7.modelo.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private static final String COLUMNAS = """
        SELECT r.id, c.dni, c.nombre AS cliente, c.telefono,
               r.id_cancha AS cancha, r.fecha, r.hora,
               r.pago, r.codigo_pago, r.monto,
               r.detalle, r.usuario, r.estado
        FROM reservas r
        JOIN clientes c ON c.id = r.id_cliente
        """;

    private Connection conectar() throws SQLException {
        Connection cn = Conexion.conectar();

        if (cn == null) {
            throw new SQLException("No se pudo conectar a MySQL.");
        }

        return cn;
    }

    private Reserva convertir(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();

        r.setId(rs.getInt("id"));
        r.setDni(rs.getString("dni"));
        r.setCliente(rs.getString("cliente"));
        r.setTelefono(rs.getString("telefono"));
        r.setCancha(rs.getInt("cancha"));
        r.setFecha(rs.getDate("fecha").toLocalDate());
        r.setHora(rs.getString("hora"));
        r.setPago(rs.getString("pago"));
        r.setCodigoPago(rs.getString("codigo_pago"));
        r.setMonto(rs.getBigDecimal("monto"));
        r.setDetalle(rs.getString("detalle"));
        r.setUsuario(rs.getString("usuario"));
        r.setEstado(rs.getString("estado"));

        return r;
    }

    public List<Reserva> listar() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();

        String sql = COLUMNAS
                + " ORDER BY r.fecha DESC, r.hora DESC, r.id DESC";

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reservas.add(convertir(rs));
            }
        }

        return reservas;
    }

    public Reserva buscarPorId(int id) throws SQLException {
        String sql = COLUMNAS + " WHERE r.id = ?";

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? convertir(rs) : null;
            }
        }
    }

    public void guardar(Reserva reserva) throws SQLException {
        registrar(reserva, false);
    }

    public void actualizar(Reserva reserva) throws SQLException {
        registrar(reserva, true);
    }

    private void registrar(Reserva reserva, boolean editar)
            throws SQLException {

        String sqlCliente = """
            INSERT INTO clientes (dni, nombre, telefono)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                nombre = VALUES(nombre),
                telefono = VALUES(telefono)
            """;

        String sqlReserva;

        if (editar) {
            sqlReserva = """
                UPDATE reservas
                SET id_cliente = ?, id_cancha = ?,
                    fecha = ?, hora = ?, pago = ?,
                    codigo_pago = ?, monto = ?, detalle = ?
                WHERE id = ?
                  AND (estado IS NULL
                       OR estado NOT IN ('Cancelado', 'Cancelada'))
                """;
        } else {
            sqlReserva = """
                INSERT INTO reservas
                (id_cliente, id_cancha, fecha, hora, pago,
                 codigo_pago, monto, detalle, usuario, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Confirmado')
                """;
        }

        try (Connection cn = conectar()) {
            cn.setAutoCommit(false);

            try {
                if (editar) {
                    String sqlBloqueo = """
                        SELECT estado FROM reservas
                        WHERE id = ? FOR UPDATE
                        """;

                    try (PreparedStatement ps =
                            cn.prepareStatement(sqlBloqueo)) {

                        ps.setInt(1, reserva.getId());

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                        "La reserva no existe.");
                            }

                            String estado = rs.getString("estado");

                            if ("Cancelado".equalsIgnoreCase(estado)
                                    || "Cancelada".equalsIgnoreCase(estado)) {

                                throw new SQLException(
                                        "La reserva ya está cancelada.");
                            }
                        }
                    }
                }

                // Registrar o actualizar los datos solo en clientes.
                try (PreparedStatement ps =
                        cn.prepareStatement(sqlCliente)) {

                    ps.setString(1, reserva.getDni());
                    ps.setString(2, reserva.getCliente());
                    ps.setString(3, reserva.getTelefono());
                    ps.executeUpdate();
                }

                // Obtener el ID del cliente para relacionar la reserva.
                int idCliente;

                String sqlIdCliente =
                        "SELECT id FROM clientes WHERE dni = ?";

                try (PreparedStatement ps =
                        cn.prepareStatement(sqlIdCliente)) {

                    ps.setString(1, reserva.getDni());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo identificar al cliente.");
                        }

                        idCliente = rs.getInt("id");
                    }
                }

                try (PreparedStatement ps =
                        cn.prepareStatement(sqlReserva)) {

                    ps.setInt(1, idCliente);
                    ps.setInt(2, reserva.getCancha());
                    ps.setDate(3,
                            java.sql.Date.valueOf(reserva.getFecha()));
                    ps.setString(4, reserva.getHora());
                    ps.setString(5, reserva.getPago());
                    ps.setString(6, reserva.getCodigoPago());
                    ps.setBigDecimal(7, reserva.getMonto());
                    ps.setString(8, reserva.getDetalle());

                    if (editar) {
                        ps.setInt(9, reserva.getId());
                    } else {
                        ps.setString(9, reserva.getUsuario());
                    }

                    ps.executeUpdate();
                }

                cn.commit();

            } catch (SQLException e) {
                try {
                    cn.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    public boolean cancelar(int id) throws SQLException {
        String sql = """
            UPDATE reservas
            SET estado = 'Cancelado'
            WHERE id = ?
              AND (estado IS NULL
                   OR estado NOT IN ('Cancelado', 'Cancelada'))
            """;

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
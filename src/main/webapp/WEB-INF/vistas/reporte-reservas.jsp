<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Arena 7 - Reporte de reservas</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

    <c:url var="panelUrl" value="/dashboard.xhtml"/>
    <c:url var="calendarioUrl" value="/calendario.xhtml"/>
    <c:url var="reporteUrl" value="/reporte-reservas"/>

    <nav class="navbar navbar-dark bg-success shadow-sm">
        <div class="container d-flex flex-wrap gap-2">
            <span class="navbar-brand fw-bold mb-0">
                Arena 7
            </span>

            <a href="${panelUrl}"
               class="btn btn-outline-light">
                Volver al panel
            </a>
        </div>
    </nav>

    <main class="container py-4">

        <div class="d-flex flex-wrap justify-content-between
                    align-items-center gap-3 mb-4">

            <div>
                <h1 class="h3 fw-bold mb-1">
                    Reporte de reservas
                </h1>

                <p class="text-muted mb-0">
                    Reservas registradas en la base de datos.
                </p>
            </div>

            <div class="d-flex flex-wrap gap-2">
                <a href="${reporteUrl}"
                   class="btn btn-success">
                    Actualizar reporte
                </a>

                <a href="${calendarioUrl}"
                   class="btn btn-outline-success">
                    Ir al calendario
                </a>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty reservas}">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-0">

                        <div class="table-responsive">
                            <table class="table table-striped
                                          table-hover align-middle mb-0">

                                <thead class="table-success">
                                    <tr>
                                        <th>ID</th>
                                        <th>DNI</th>
                                        <th>Cliente</th>
                                        <th>Cancha</th>
                                        <th>Fecha</th>
                                        <th>Hora</th>
                                        <th>Pago</th>
                                        <th>Monto (S/)</th>
                                        <th>Estado</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach var="reserva"
                                               items="${reservas}">
                                        <tr>
                                            <td>
                                                <c:out value="${reserva.id}"/>
                                            </td>

                                            <td>
                                                <c:out value="${reserva.dni}"/>
                                            </td>

                                            <td>
                                                <c:out value="${reserva.cliente}"/>
                                            </td>

                                            <td class="text-nowrap">
                                                Cancha
                                                <c:out value="${reserva.cancha}"/>
                                            </td>

                                            <td class="text-nowrap">
                                                <c:out value="${reserva.fecha}"/>
                                            </td>

                                            <td class="text-nowrap">
                                                <c:out value="${reserva.horaFormateada}"/>
                                            </td>

                                            <td>
                                                <c:out value="${reserva.pago}"/>
                                            </td>

                                            <td>
                                                <c:out value="${reserva.monto}"/>
                                            </td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${reserva.cancelada}">
                                                        <span class="badge bg-danger">
                                                            <c:out value="${reserva.estado}"/>
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="badge bg-success">
                                                            <c:out value="${reserva.estado}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>

                            </table>
                        </div>

                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <c:if test="${empty error}">
                    <div class="alert alert-info">
                        No hay reservas registradas.
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>

    </main>

</body>
</html>
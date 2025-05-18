<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- Importar la librería JSTL core --%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Página Principal - Municipios</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
            color: #333;
        }

        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            max-width: 1000px; /* Aumenta el ancho máximo para más columnas */
            margin: 0 auto; /* Centrar el contenedor */
            overflow-x: auto; /* Añade scroll horizontal si la tabla es muy ancha */
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse; /* Elimina el espacio entre bordes */
            margin-bottom: 20px;
            min-width: 800px; /* Asegura un ancho mínimo para la tabla si hay muchas columnas */
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
            vertical-align: top; /* Alinea el contenido arriba */
        }

        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }

        tr:nth-child(even) { /* Color de fondo para filas pares */
            background-color: #f9f9f9;
        }

         /* Estilo específico para celdas de descripción si son largas */
         td.descripcion {
             max-width: 200px; /* Ancho máximo para la descripción */
             white-space: normal; /* Permite que el texto salte de línea */
             word-wrap: break-word; /* Rompe palabras largas si es necesario */
         }


        .button-container {
            text-align: center; /* Centrar los botones */
            margin-top: 20px;
        }

        .button-container a,
        .button-container button {
            display: inline-block; /* Mostrar en línea */
            padding: 10px 15px;
            margin: 0 10px; /* Espacio entre botones */
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
            text-decoration: none; /* Quitar subrayado de enlaces */
            color: white;
            transition: background-color 0.3s ease;
        }

        .add-button {
            background-color: #5cb85c; /* Verde para agregar */
        }
        .add-button:hover {
            background-color: #4cae4c;
        }

        .logout-button {
            background-color: #d9534f; /* Rojo para cerrar sesión */
        }
        .logout-button:hover {
            background-color: #c9302c;
        }

        /* Estilos para los mensajes (opcional, si quieres mostrar mensajes en home) */
         .message {
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 4px;
            font-weight: bold;
            text-align: center;
        }
        .success-message {
            color: #3c763d;
            background-color: #dff0d8;
            border: 1px solid #d6e9c6;
        }
         .error-message {
            color: #a94442;
            background-color: #f2dede;
            border: 1px solid #ebccd1;
        }

    </style>
</head>
<body>
    <div class="container">
        <h1>Listado de Municipios</h1>

        <%-- Puedes mostrar mensajes aquí si el controlador los pone en el request --%>
        <%-- Por ejemplo, si añades lógica para mostrar éxito al agregar municipio --%>
        <%
            String message = (String) request.getAttribute("message");
            String messageType = (String) request.getAttribute("messageType");
            if (message != null) {
                String cssClass = "message " + (messageType != null ? messageType + "-message" : "");
        %>
                <p class="<%= cssClass %>"><%= message %></p>
        <%
            }
        %>


        <%-- Mostrar la tabla de municipios --%>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Departamento</th>
                    <th>País</th>
                    <th>Alcalde</th>
                    <th>Gobernador</th>
                    <th>Patrono Religioso</th>
                    <th>Habitantes</th>
                    <th>Casas</th>
                    <th>Parques</th>
                    <th>Colegios</th>
                    <th>Descripción</th>
                    <%-- Puedes añadir columnas para acciones (Editar/Eliminar) aquí más adelante --%>
                </tr>
            </thead>
            <tbody>
                <%-- Usar JSTL para iterar sobre la lista de municipios --%>
                <c:forEach var="municipio" items="${municipios}">
                    <tr>
                        <td><c:out value="${municipio.id}"/></td>
                        <td><c:out value="${municipio.nombre}"/></td>
                        <td><c:out value="${municipio.departamento}"/></td>
                        <td><c:out value="${municipio.pais}"/></td>
                        <td><c:out value="${municipio.alcalde}"/></td>
                        <td><c:out value="${municipio.gobernador}"/></td>
                        <td><c:out value="${municipio.patronoReligioso}"/></td>
                        <td><c:out value="${municipio.numHabitantes}"/></td>
                        <td><c:out value="${municipio.numCasas}"/></td>
                        <td><c:out value="${municipio.numParques}"/></td>
                        <td><c:out value="${municipio.numColegios}"/></td>
                        <td class="descripcion"><c:out value="${municipio.descripcion}"/></td>
                        <%-- Puedes añadir celdas para botones de acción (Editar/Eliminar) aquí --%>
                    </tr>
                </c:forEach>
                 <%-- Si la lista está vacía --%>
                 <c:if test="${empty municipios}">
                     <tr>
                         <%-- Ajusta colspan al número total de columnas --%>
                         <td colspan="12" style="text-align: center;">No hay municipios registrados.</td>
                     </tr>
                 </c:if>
            </tbody>
        </table>

        <div class="button-container">
            <%-- Botón para agregar nuevo municipio (redirige a otra página/formulario) --%>
            <%-- Necesitarás crear un Servlet/JSP para agregar municipios (ej. AddMunicipioController/addMunicipio.jsp) --%>
            <a href="addMunicipio" class="add-button">Agregar Nuevo Municipio</a> <%-- Ajusta la URL si tu controlador/página se llama diferente --%>

            <%-- Botón para cerrar sesión --%>
            <%-- Apunta al LogoutController --%>
            <button onclick="location.href='logout'" class="logout-button">Cerrar Sesión</button> <%-- Ajusta la URL si tu controlador se llama diferente --%>
        </div>

    </div>
</body>
</html>

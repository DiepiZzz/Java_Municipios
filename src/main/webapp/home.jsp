<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
            max-width: 1200px;
            margin: 0 auto;
            overflow-x: auto;
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            min-width: 1000px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
            vertical-align: top;
        }

        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }

        tr:nth-child(even) {
            background-color: #f9f9f9;
        }

         td.descripcion {
             max-width: 200px;
             white-space: normal;
             word-wrap: break-word;
         }

        td .action-button {
            display: inline-block;
            padding: 5px 10px;
            margin-right: 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9em;
            text-decoration: none;
            color: white;
            transition: background-color 0.3s ease;
        }

        td .edit-button {
            background-color: #f0ad4e;
        }
        td .edit-button:hover {
            background-color: #ec971f;
        }

        td .delete-button {
            background-color: #d9534f;
        }
         td .delete-button:hover {
            background-color: #c9302c;
        }


        .button-container {
            text-align: center;
            margin-top: 20px;
        }

        .button-container a,
        .button-container button {
            display: inline-block;
            padding: 10px 15px;
            margin: 0 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
            text-decoration: none;
            color: white;
            transition: background-color 0.3s ease;
        }

        .add-button {
            background-color: #5cb85c;
        }
        .add-button:hover {
            background-color: #4cae4c;
        }

         .back-button {
             background-color: #0275d8;
         }
         .back-button:hover {
             background-color: #025aa5;
         }


        .logout-button {
            background-color: #d9534f;
        }
        .logout-button:hover {
            background-color: #c9302c;
        }

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
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
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
                        <td>
                            <a href="editMunicipio?id=<c:out value='${municipio.id}'/>" class="action-button edit-button">Editar</a>
                            <form action="deleteMunicipio" method="POST" style="display:inline;">
                                <input type="hidden" name="id" value="<c:out value='${municipio.id}'/>">
                                <button type="submit" class="action-button delete-button" onclick="return confirm('¿Estás seguro de que quieres eliminar este municipio: ${municipio.nombre}?');">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                 <c:if test="${empty municipios}">
                     <tr>
                         <td colspan="13" style="text-align: center;">No hay municipios registrados.</td>
                     </tr>
                 </c:if>
            </tbody>
        </table>

        <div class="button-container">
            <a href="addMunicipio" class="add-button">Agregar Nuevo Municipio</a>

            <a href="graphics" class="back-button">Ver Gráficos</a>

            <button onclick="location.href='logout'" class="logout-button">Cerrar Sesión</button>
        </div>

    </div>
</body>
</html>

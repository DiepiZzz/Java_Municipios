<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Editar Municipio</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
            color: #333;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 90vh;
        }

        .container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            width: 100%;
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group textarea {
            width: calc(100% - 22px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
            box-sizing: border-box;
        }

         .form-group textarea {
             resize: vertical;
             min-height: 100px;
         }


        button {
            background-color: #5cb85c;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1.1em;
            width: 100%;
            transition: background-color 0.3s ease;
            margin-top: 10px;
        }

        button:hover {
            background-color: #4cae4c;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #0275d8;
            text-decoration: none;
            font-size: 1em;
        }
        .back-link:hover {
             text-decoration: underline;
         }

         .error-message {
            color: #a94242;
            background-color: #f2dede;
            border: 1px solid #ebccd1;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            font-weight: bold;
            text-align: center;
        }

    </style>
</head>
<body>
    <div class="container">
        <h1>Editar Municipio</h1>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
                <p class="error-message"><%= errorMessage %></p>
        <%
            }
        %>

        <c:set var="municipio" value="${requestScope.municipio}"/>

        <form action="editMunicipio" method="POST">

            <input type="hidden" name="id" value="<c:out value='${municipio.id}'/>">

            <div class="form-group">
                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" required value="<c:out value="${municipio.nombre}"/>">
            </div>

            <div class="form-group">
                <label for="departamento">Departamento:</label>
                <input type="text" id="departamento" name="departamento" required value="<c:out value="${municipio.departamento}"/>">
            </div>

            <div class="form-group">
                <label for="pais">País:</label>
                <input type="text" id="pais" name="pais" required value="<c:out value="${municipio.pais}"/>">
            </div>

            <div class="form-group">
                <label for="alcalde">Alcalde:</label>
                <input type="text" id="alcalde" name="alcalde" value="<c:out value="${municipio.alcalde}"/>">
            </div>

            <div class="form-group">
                <label for="gobernador">Gobernador:</label>
                <input type="text" id="gobernador" name="gobernador" value="<c:out value="${municipio.gobernador}"/>">
            </div>

            <div class="form-group">
                <label for="patronoReligioso">Patrono Religioso:</label>
                <input type="text" id="patronoReligioso" name="patronoReligioso" value="<c:out value="${municipio.patronoReligioso}"/>">
            </div>

            <div class="form-group">
                <label for="numHabitantes">Número de Habitantes:</label>
                <input type="number" id="numHabitantes" name="numHabitantes" min="0" value="<c:out value="${municipio.numHabitantes}"/>">
            </div>

            <div class="form-group">
                <label for="numCasas">Número de Casas:</label>
                <input type="number" id="numCasas" name="numCasas" min="0" value="<c:out value="${municipio.numCasas}"/>">
            </div>

            <div class="form-group">
                <label for="numParques">Número de Parques:</label>
                <input type="number" id="numParques" name="numParques" min="0" value="<c:out value="${municipio.numParques}"/>">
            </div>

            <div class="form-group">
                <label for="numColegios">Número de Colegios:</label>
                <input type="number" id="numColegios" name="numColegios" min="0" value="<c:out value="${municipio.numColegios}"/>">
            </div>

            <div class="form-group">
                <label for="descripcion">Descripción:</label>
                <textarea id="descripcion" name="descripcion"><c:out value="${municipio.descripcion}"/></textarea>
            </div>

            <button type="submit">Guardar Cambios</button>
        </form>

        <a href="home" class="back-link">Cancelar y Volver al Listado</a>

    </div>
</body>
</html>

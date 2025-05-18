<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Agregar Nuevo Municipio</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
            color: #333;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 90vh; /* Ajusta para centrar verticalmente */
        }

        .container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            max-width: 600px; /* Ancho máximo para el formulario */
            width: 100%; /* Ocupa todo el ancho disponible hasta el max-width */
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
        .form-group textarea { /* Estilo para inputs de texto, número y textarea */
            width: calc(100% - 22px); /* Ancho ajustado por padding y borde */
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
            box-sizing: border-box; /* Incluye padding y borde en el ancho total */
        }

         .form-group textarea {
             resize: vertical; /* Permite redimensionar solo verticalmente */
             min-height: 100px; /* Altura mínima para la descripción */
         }


        button {
            background-color: #5cb85c; /* Verde para guardar */
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1.1em;
            width: 100%;
            transition: background-color 0.3s ease;
            margin-top: 10px; /* Espacio encima del botón */
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

        /* Estilos para mensajes de error */
         .error-message {
            color: #a94442; /* Rojo oscuro */
            background-color: #f2dede; /* Fondo rojo claro */
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
        <h1>Agregar Nuevo Municipio</h1>

        <%-- Mostrar mensaje de error si existe (puesto por el Servlet) --%>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
                <p class="error-message"><%= errorMessage %></p>
        <%
            }
        %>


        <%-- Formulario para agregar municipio --%>
        <form action="addMunicipio" method="POST"> <%-- Apunta al AddMunicipioController, método POST --%>

            <div class="form-group">
                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" required value="<%= request.getAttribute("nombre") != null ? request.getAttribute("nombre") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="departamento">Departamento:</label>
                <input type="text" id="departamento" name="departamento" required value="<%= request.getAttribute("departamento") != null ? request.getAttribute("departamento") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="pais">País:</label>
                <input type="text" id="pais" name="pais" required value="<%= request.getAttribute("pais") != null ? request.getAttribute("pais") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="alcalde">Alcalde:</label>
                <input type="text" id="alcalde" name="alcalde" value="<%= request.getAttribute("alcalde") != null ? request.getAttribute("alcalde") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="gobernador">Gobernador:</label>
                <input type="text" id="gobernador" name="gobernador" value="<%= request.getAttribute("gobernador") != null ? request.getAttribute("gobernador") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="patronoReligioso">Patrono Religioso:</label>
                <input type="text" id="patronoReligioso" name="patronoReligioso" value="<%= request.getAttribute("patronoReligioso") != null ? request.getAttribute("patronoReligioso") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="numHabitantes">Número de Habitantes:</label>
                <input type="number" id="numHabitantes" name="numHabitantes" min="0" value="<%= request.getAttribute("numHabitantes") != null ? request.getAttribute("numHabitantes") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="numCasas">Número de Casas:</label>
                <input type="number" id="numCasas" name="numCasas" min="0" value="<%= request.getAttribute("numCasas") != null ? request.getAttribute("numCasas") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="numParques">Número de Parques:</label>
                <input type="number" id="numParques" name="numParques" min="0" value="<%= request.getAttribute("numParques") != null ? request.getAttribute("numParques") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="numColegios">Número de Colegios:</label>
                <input type="number" id="numColegios" name="numColegios" min="0" value="<%= request.getAttribute("numColegios") != null ? request.getAttribute("numColegios") : "" %>"> <%-- Preserva valor si hay error --%>
            </div>

            <div class="form-group">
                <label for="descripcion">Descripción:</label>
                <textarea id="descripcion" name="descripcion"><%= request.getAttribute("descripcion") != null ? request.getAttribute("descripcion") : "" %></textarea> <%-- Preserva valor si hay error --%>
            </div>

            <button type="submit">Guardar Municipio</button>
        </form>

        <%-- Enlace para volver a la página principal --%>
        <a href="home" class="back-link">Volver al Listado de Municipios</a>

    </div>
</body>
</html>

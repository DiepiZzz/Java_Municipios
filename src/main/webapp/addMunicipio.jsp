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
            color: #a94442;
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
        <h1>Agregar Nuevo Municipio</h1>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
                <p class="error-message"><%= errorMessage %></p>
        <%
            }
        %>


        <form action="addMunicipio" method="POST">

            <div class="form-group">
                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" required value="<%= request.getAttribute("nombre") != null ? request.getAttribute("nombre") : "" %>">
            </div>

            <div class="form-group">
                <label for="departamento">Departamento:</label>
                <input type="text" id="departamento" name="departamento" required value="<%= request.getAttribute("departamento") != null ? request.getAttribute("departamento") : "" %>">
            </div>

            <div class="form-group">
                <label for="pais">País:</label>
                <input type="text" id="pais" name="pais" required value="<%= request.getAttribute("pais") != null ? request.getAttribute("pais") : "" %>">
            </div>

            <div class="form-group">
                <label for="alcalde">Alcalde:</label>
                <input type="text" id="alcalde" name="alcalde" value="<%= request.getAttribute("alcalde") != null ? request.getAttribute("alcalde") : "" %>">
            </div>

            <div class="form-group">
                <label for="gobernador">Gobernador:</label>
                <input type="text" id="gobernador" name="gobernador" value="<%= request.getAttribute("gobernador") != null ? request.getAttribute("gobernador") : "" %>">
            </div>

            <div class="form-group">
                <label for="patronoReligioso">Patrono Religioso:</label>
                <input type="text" id="patronoReligioso" name="patronoReligioso" value="<%= request.getAttribute("patronoReligioso") != null ? request.getAttribute("patronoReligioso") : "" %>">
            </div>

            <div class="form-group">
                <label for="numHabitantes">Número de Habitantes:</label>
                <input type="number" id="numHabitantes" name="numHabitantes" min="0" value="<%= request.getAttribute("numHabitantes") != null ? request.getAttribute("numHabitantes") : "" %>">
            </div>

            <div class="form-group">
                <label for="numCasas">Número de Casas:</label>
                <input type="number" id="numCasas" name="numCasas" min="0" value="<%= request.getAttribute("numCasas") != null ? request.getAttribute("numCasas") : "" %>">
            </div>

            <div class="form-group">
                <label for="numParques">Número de Parques:</label>
                <input type="number" id="numParques" name="numParques" min="0" value="<%= request.getAttribute("numParques") != null ? request.getAttribute("numParques") : "" %>">
            </div>

            <div class="form-group">
                <label for="numColegios">Número de Colegios:</label>
                <input type="number" id="numColegios" name="numColegios" min="0" value="<%= request.getAttribute("numColegios") != null ? request.getAttribute("numColegios") : "" %>">
            </div>

            <div class="form-group">
                <label for="descripcion">Descripción:</label>
                <textarea id="descripcion" name="descripcion"><%= request.getAttribute("descripcion") != null ? request.getAttribute("descripcion") : "" %></textarea>
            </div>

            <button type="submit">Guardar Municipio</button>
        </form>

        <a href="home" class="back-link">Volver al Listado de Municipios</a>

    </div>
</body>
</html>

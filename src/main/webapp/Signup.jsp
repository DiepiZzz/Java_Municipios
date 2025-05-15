<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Crear Nuevo Usuario</title>
     <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 80vh;
            margin: 0;
        }

        .create-container { /* Nuevo nombre de clase para este contenedor */
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            width: 300px;
            text-align: center;
        }

        h2 {
            margin-bottom: 20px;
            color: #333;
        }

        .form-group {
            margin-bottom: 15px;
            text-align: left;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }

        .form-group input[type="text"],
        .form-group input[type="password"],
        .form-group input[type="email"] {
            width: calc(100% - 20px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4x;
            font-size: 1em;
        }

        .error-message { /* Para errores de validación o existencia */
            color: red;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message { /* Para mensajes de éxito (menos común en esta página, podrías redirigir) */
             color: green;
             margin-bottom: 15px;
             font-weight: bold;
        }

        button {
            background-color: #0275d8; /* Azul para crear */
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1.1em;
            width: 100%;
            transition: background-color 0.3s ease;
             margin-bottom: 10px;
        }

        button:hover {
            background-color: #025aa5;
        }

         .back-link { /* Enlace para volver al login */
            display: block;
            margin-top: 15px;
            color: #555;
            text-decoration: none;
        }
         .back-link:hover {
             text-decoration: underline;
         }

    </style>
</head>
<body>
    <div class="create-container">
        <h2>Crear Nuevo Usuario</h2>

         <%-- Mostrar mensaje de Creación si existe (error o éxito) --%>
        <%
            String creationMessage = (String) request.getAttribute("creationMessage");
            String messageType = (String) request.getAttribute("messageType"); // Para saber si es error o éxito

            if (creationMessage != null) {
                String cssClass = "error-message"; // Por defecto, rojo para error
                if (messageType != null && messageType.equals("success")) {
                    cssClass = "success-message"; // Si es éxito, verde
                }
        %>
                <p class="<%= cssClass %>"><%= creationMessage %></p>
        <%
            }
        %>

        <form action="Signup" method="POST"> <%-- Apunta al mismo Servlet, método POST --%>
            <div class="form-group">
                <label for="username">Usuario:</label>
                <input type="text" id="username" name="username" required value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"> <%-- Preserva valor si falla --%>
            </div>
             <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required> <%-- No preservar password por seguridad --%>
            </div>
             <div class="form-group">
                <label for="nombre">Nombre Completo:</label>
                <input type="text" id="nombre" name="nombre" required value="<%= request.getAttribute("nombre") != null ? request.getAttribute("nombre") : "" %>"> <%-- Preserva valor si falla --%>
            </div>
             <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"> <%-- Preserva valor si falla --%>
            </div>
            <button type="submit">Crear Usuario</button>
        </form>

        <%-- Enlace para volver a la página de inicio de sesión --%>
        <a href="login.jsp" class="back-link">Volver al inicio de sesión</a>

    </div>
</body>
</html>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Recuperar Contraseña</title>
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

        .recovery-container { /* Clase para este contenedor */
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

        .form-group input[type="email"] { /* Solo campo de email */
            width: calc(100% - 20px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
        }

        .error-message { /* Para errores de validación o envío */
            color: red;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message { /* Para el mensaje genérico de éxito */
             color: green;
             margin-bottom: 15px;
             font-weight: bold;
        }

        button {
            background-color: #f0ad4e; /* Naranja o similar para recuperar */
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
            background-color: #ec971f; /* Naranja más oscuro */
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
    <div class="recovery-container">
        <h2>Recuperar Contraseña</h2>

         <%-- Mostrar mensaje si existe (error o éxito) --%>
        <%
            String recoveryMessage = (String) request.getAttribute("recoveryMessage");
            String messageType = (String) request.getAttribute("messageType"); // Para saber si es error o éxito

            if (recoveryMessage != null) {
                String cssClass = "error-message"; // Por defecto
                if (messageType != null && messageType.equals("success")) {
                    cssClass = "success-message"; // Si es éxito
                }
        %>
                <p class="<%= cssClass %>"><%= recoveryMessage %></p>
        <%
            }
        %>

        <form action="forgotPassword" method="POST"> <%-- Apunta al controlador, método POST --%>
            <div class="form-group">
                <label for="email">Ingresa tu email:</label>
                <input type="email" id="email" name="email" required value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"> <%-- Preserva valor si falla --%>
            </div>
            <button type="submit">Enviar Enlace de Recuperación</button>
        </form>

        <%-- Enlace para volver a la página de inicio de sesión --%>
        <a href="login.jsp" class="back-link">Volver al inicio de sesión</a>

    </div>
</body>
</html>
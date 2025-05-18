<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Restablecer Contraseña</title>
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

        .reset-container { /* Clase para este contenedor */
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            width: 350px; /* Un poco más ancho para los campos de password */
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

        .form-group input[type="password"] { /* Solo campos de password */
             width: calc(100% - 20px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
        }

        .message { /* Clase genérica para mensajes */
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 4px;
            font-weight: bold;
        }

        .error-message { /* Para errores */
            color: #a94442; /* Rojo oscuro */
            background-color: #f2dede; /* Fondo rojo claro */
            border: 1px solid #ebccd1;
        }
        .success-message { /* Para mensajes de éxito */
            color: #3c763d; /* Verde oscuro */
            background-color: #dff0d8; /* Fondo verde claro */
            border: 1px solid #d6e9c6;
        }

        button {
            background-color: #5bc0de; /* Azul para restablecer */
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
            background-color: #31b0d5; /* Azul más oscuro */
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
    <div class="reset-container">
        <h2>Restablecer Contraseña</h2>

        <%-- Mostrar mensaje si existe (error o éxito) --%>
        <%
            String resetMessage = (String) request.getAttribute("resetMessage");
            String messageType = (String) request.getAttribute("messageType"); // Para saber si es error o éxito

            if (resetMessage != null) {
                String cssClass = "message "; // Clase base
                if (messageType != null) {
                    cssClass += messageType + "-message"; // Añade la clase específica (error-message o success-message)
                } else {
                    cssClass += "error-message"; // Por defecto si no se especifica tipo
                }
        %>
                <p class="<%= cssClass %>"><%= resetMessage %></p>
        <%
            }
        %>

        <%--
            Si llegamos aquí con un token válido (pasado por el ResetPasswordController
            en el request.setAttribute("token", token);), mostramos el formulario.
            Si el token no fue válido (y el controlador reenvió a login.jsp),
            esta parte no se mostrará.
        --%>
        <% String token = (String) request.getAttribute("token"); %>
        <% if (token != null && !token.trim().isEmpty()) { %>

             <form action="resetPassword" method="POST"> <%-- Apunta al ResetPasswordController, método POST --%>

                <%-- Campo oculto para enviar el token de vuelta al servidor --%>
                <input type="hidden" name="token" value="<%= token %>">

                <div class="form-group">
                    <label for="newPassword">Nueva Contraseña:</label>
                    <input type="password" id="newPassword" name="newPassword" required>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Confirmar Contraseña:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>

                <button type="submit">Restablecer Contraseña</button>
            </form>

        <% } else { %>
             <%-- Si no hay token en el request (lo cual no debería pasar si el doGet funcionó bien),
                  podrías mostrar un mensaje o redirigir. Pero el controlador ya maneja tokens inválidos.
                  Este else es más bien una red de seguridad visual. --%>
             <p class="error-message">Error: No se proporcionó un token válido.</p>
        <% } %>


        <%-- Enlace para volver a la página de inicio de sesión --%>
        <a href="login.jsp" class="back-link">Volver al inicio de sesión</a>

    </div>
</body>
</html>

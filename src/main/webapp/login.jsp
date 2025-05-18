<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Iniciar Sesión</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            flex-direction: column; /* Permite apilar elementos como el contenedor y el enlace */
            justify-content: center;
            align-items: center;
            min-height: 80vh;
            margin: 0;
        }

        .login-container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            width: 300px;
            text-align: center;
            margin-bottom: 15px; /* Espacio debajo del contenedor del formulario */
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
        .form-group input[type="password"] {
            width: calc(100% - 20px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
        }

        /* Estilos para los mensajes */
        .error-message {
            color: #a94442; /* Rojo oscuro */
            background-color: #f2dede; /* Fondo rojo claro */
            border: 1px solid #ebccd1;
            padding: 10px; /* Añadido padding para que se vea mejor */
            border-radius: 4px; /* Añadido border-radius */
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message { /* Estilo para mensajes de éxito */
             color: #3c763d; /* Verde oscuro */
            background-color: #dff0d8; /* Fondo verde claro */
            border: 1px solid #d6e9c6;
            padding: 10px; /* Añadido padding */
            border-radius: 4px; /* Añadido border-radius */
            margin-bottom: 15px;
            font-weight: bold;
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
            margin-bottom: 10px; /* Espacio debajo del botón */
        }

        button:hover {
            background-color: #4cae4c;
        }

        .Sign-up-link, .forgot-password-link { /* Estilo para enlaces debajo del formulario */
            display: block;
            margin-top: 5px; /* Menos margen para que estén más cerca */
            color: #0275d8;
            text-decoration: none;
            font-size: 0.9em; /* Letra un poco más pequeña */
        }
         .Sign-up-link:hover, .forgot-password-link:hover {
             text-decoration: underline;
         }

    </style>
</head>
<body>
    <div class="login-container">
        <h2>Iniciar Sesión</h2>

        <%-- *** BLOQUE JSP PARA MOSTRAR MENSAJES (Error de Login, Éxito de Creación, Éxito de Reset) *** --%>
        <%
            // 1. Mensaje de error de login (si viene del LoginController como atributo)
            String loginErrorMessage = (String) request.getAttribute("errorMessage");

            // 2. Mensaje de éxito de creación (si viene de CreateUsuarioController como atributo)
            String creationSuccessMessage = (String) request.getAttribute("creationSuccessMessage");

            // 3. Mensaje de éxito de restablecimiento de contraseña (si viene del ResetPasswordController como parámetro de URL)
            String resetSuccessParam = request.getParameter("resetSuccess");
            boolean resetSuccess = "true".equals(resetSuccessParam);

            if (loginErrorMessage != null) {
                // Mostrar error de login
        %>
                <p class="error-message"><%= loginErrorMessage %></p>
        <%
            } else if (creationSuccessMessage != null) {
                // Mostrar mensaje de éxito de creación
        %>
                <p class="success-message"><%= creationSuccessMessage %></p>
        <%
            } else if (resetSuccess) {
                // Mostrar mensaje de éxito de restablecimiento de contraseña
        %>
                <p class="success-message">Contraseña recuperada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña.</p>
        <%
            }
            // Nota: Los mensajes de error de validación del formulario de login (ej. campos vacíos)
            // deberían manejarse en el LoginController antes de llegar aquí, o con JS en el cliente.
        %>
        <%-- *** FIN DEL BLOQUE DE MENSAJES *** --%>


        <form action="login" method="POST">
            <div class="form-group">
                <label for="username">Usuario:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit">Ingresar</button>
        </form>

        <div class="links">
            <%-- Enlace para ir a la página de crear usuario --%>
            <%-- Asegúrate de que esta URL "Signup" coincida con el mapeo de tu CreateUsuarioController --%>
            <a href="Signup" class="Sign-up-link">¿No tienes cuenta? Crea una aquí.</a>

            <%-- Enlace para ir a la página de olvidé mi contraseña --%>
            <%-- Asegúrate de que esta URL "forgotPassword" coincida con el mapeo de tu ForgotPasswordController --%>
            <a href="forgotPassword" class="forgot-password-link">¿Olvidaste tu contraseña?</a>
        </div>

    </div>
    <%-- Aquí podrías añadir otros contenedores si tuvieras más formularios apilados,
         pero ahora la creación va en otra página. --%>

</body>
</html>
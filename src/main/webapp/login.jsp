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
            justify-content: center;
            align-items: center;
            min-height: 80vh; /* Centra verticalmente */
            margin: 0;
        }

        .login-container {
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
        .form-group input[type="password"] {
            width: calc(100% - 20px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
        }

        .error-message {
            color: red;
            margin-bottom: 15px;
            font-weight: bold;
        }

        button {
            background-color: #5cb85c; /* Verde */
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

        /* Estilo para el enlace o botón de crear usuario */
        .create-link {
            display: block; /* Para que ocupe su propia línea */
            margin-top: 15px;
            color: #0275d8; /* Azul */
            text-decoration: none;
        }
         .create-link:hover {
             text-decoration: underline;
         }

    </style>
</head>
<body>
    <div class="login-container">
        <h2>Iniciar Sesión</h2>

        <%-- Mostrar mensaje de error de Login si existe --%>
        <%
            String loginErrorMessage = (String) request.getAttribute("errorMessage");
            if (loginErrorMessage != null) {
        %>
                <p class="error-message"><%= loginErrorMessage %></p>
        <%
            }
        %>
         <%-- Mostrar mensaje de éxito de Creación si vienes desde la página de creación --%>
         <%
            String creationSuccessMessage = (String) request.getAttribute("creationSuccessMessage");
            if (creationSuccessMessage != null) {
         %>
                <p class="success-message"><%= creationSuccessMessage %></p> <%-- Necesitas definir .success-message en tu CSS --%>
         <%
            }
         %>


        <form action="login" method="POST"> <%-- La acción debe apuntar a tu Servlet de login --%>
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

        <%-- Enlace o botón para ir a la página de crear usuario --%>
        <a href="Signup" class="create-link">¿No tienes cuenta? Crea una aquí.</a>
      
    </div>
</body>
</html>
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

        .reset-container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            width: 350px;
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

        .form-group input[type="password"] {
            width: calc(100% - 20px);
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
        }

        .message {
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 4px;
            font-weight: bold;
        }

        .error-message {
            color: #a94442;
            background-color: #f2dede;
            border: 1px solid #ebccd1;
        }
        .success-message {
            color: #3c763d;
            background-color: #dff0d8;
            border: 1px solid #d6e9c6;
        }

        button {
            background-color: #5bc0de;
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
            background-color: #31b0d5;
        }

        .back-link {
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

        <%
            String resetMessage = (String) request.getAttribute("resetMessage");
            String messageType = (String) request.getAttribute("messageType");

            if (resetMessage != null) {
                String cssClass = "message ";
                if (messageType != null) {
                    cssClass += messageType + "-message";
                } else {
                    cssClass += "error-message";
                }
        %>
                <p class="<%= cssClass %>"><%= resetMessage %></p>
        <%
            }
        %>

        <% String token = (String) request.getAttribute("token"); %>
        <% if (token != null && !token.trim().isEmpty()) { %>

            <form action="resetPassword" method="POST">

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
            <p class="error-message">Error: No se proporcionó un token válido.</p>
        <% } %>


        <a href="login.jsp" class="back-link">Volver al inicio de sesión</a>

    </div>
</body>
</html>

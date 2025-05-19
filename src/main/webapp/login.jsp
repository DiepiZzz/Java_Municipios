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
            flex-direction: column; 
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
            margin-bottom: 15px; 
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
            color: #a94442; 
            background-color: #f2dede; 
            border: 1px solid #ebccd1;
            padding: 10px; 
            border-radius: 4px; 
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message { 
             color: #3c763d;
            background-color: #dff0d8; 
            border: 1px solid #d6e9c6;
            padding: 10px; 
            border-radius: 4px;
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
            margin-bottom: 10px;
        }

        button:hover {
            background-color: #4cae4c;
        }

        .Sign-up-link, .forgot-password-link { 
            display: block;
            margin-top: 5px; 
            color: #0275d8;
            text-decoration: none;
            font-size: 0.9em; 
        }
         .Sign-up-link:hover, .forgot-password-link:hover {
             text-decoration: underline;
         }

    </style>
</head>
<body>
    <div class="login-container">
        <h2>Iniciar Sesión</h2>

     
        <%
           
            String loginErrorMessage = (String) request.getAttribute("errorMessage");

         
            String creationSuccessMessage = (String) request.getAttribute("creationSuccessMessage");

           
            String resetSuccessParam = request.getParameter("resetSuccess");
            boolean resetSuccess = "true".equals(resetSuccessParam);

           
            String cssClass = ""; 

            if (loginErrorMessage != null) {
                cssClass = "error-message"; 
        %>
                <p class="<%= cssClass %>"><%= loginErrorMessage %></p>
        <%
            } else if (creationSuccessMessage != null) {
                cssClass = "success-message"; 
        %>
                <p class="<%= cssClass %>"><%= creationSuccessMessage %></p>
        <%
            } else if (resetSuccess) {
              
                cssClass = "success-message"; 
        %>
                <p class="<%= cssClass %>">Contraseña recuperada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña.</p>
        <%
            }
           
        %>
       


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
            
            <a href="Signup" class="Sign-up-link">¿No tienes cuenta? Crea una aquí.</a>

           
            <a href="forgotPassword" class="forgot-password-link">¿Olvidaste tu contraseña?</a>
        </div>

    </div>
   

</body>
</html>

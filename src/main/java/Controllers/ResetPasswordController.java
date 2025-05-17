// Asegúrate de que este es el nombre del paquete correcto en tu estructura
package Controllers;

import Model.Entities.Usuario; // Importar la entidad Usuario
import Model.Services.UsuarioService; // Importar tu servicio de Usuario


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet que maneja la validación del token de recuperación y el restablecimiento de contraseña.
 * Mapeado a la URL /resetPassword (la URL en el enlace del email).
 */
@WebServlet("/resetPassword") // <<-- Mapea este Servlet a la URL "/resetPassword"
public class ResetPasswordController extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService(); // Instanciar el servicio
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Se llama cuando el usuario hace clic en el enlace del email (viene con el token en la URL).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener el token de los parámetros de la URL
        String token = request.getParameter("token");

        // 2. Validar el token usando el servicio
        // El servicio verifica si el token existe, no está usado y no ha expirado.
        Usuario usuarioAsociado = usuarioService.validateRecoveryToken(token);

        // 3. Procesar el resultado de la validación
        if (usuarioAsociado != null) {
            // Token válido. Mostrar el formulario para ingresar nueva contraseña.
            // Es crucial pasar el token a la vista para que el formulario POST lo incluya.
            request.setAttribute("token", token);
            request.setAttribute("usuario", usuarioAsociado); // Opcional: pasar el usuario si necesitas mostrar info

            // Reenviar a la página JSP del formulario de restablecimiento.
            // Necesitas crear esta página JSP (ej. resetPassword.jsp)
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response); // <<-- ASEGÚRATE DE CREAR resetPassword.jsp

        } else {
            // Token inválido, expirado o ya usado. Mostrar un mensaje de error.
            request.setAttribute("resetMessage", "El enlace de restablecimiento de contraseña es inválido o ha expirado.");
            request.setAttribute("messageType", "error");
            // Reenviar a una página de error genérica, o a la página de login
            // para que el usuario pueda solicitar otro enlace.
            request.getRequestDispatcher("/login.jsp").forward(request, response); // <<-- Podrías reenviar a login o a una página de error dedicada
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Se llama cuando el usuario envía el formulario con la nueva contraseña.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener el token (de un campo oculto en el formulario) y las nuevas contraseñas
        String token = request.getParameter("token"); // Asegúrate de que el formulario en resetPassword.jsp incluya un campo oculto para el token
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 2. Validar la entrada del usuario
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
             request.setAttribute("resetMessage", "Token, nueva contraseña o confirmación están vacíos.");
             request.setAttribute("messageType", "error");
             request.setAttribute("token", token); // Preservar el token si es nulo/vacío
             // Reenviar de vuelta al formulario de restablecimiento
             request.getRequestDispatcher("/resetPassword.jsp").forward(request, response); // <<-- AJUSTA LA RUTA DEL JSP
             return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("resetMessage", "La nueva contraseña y la confirmación no coinciden.");
            request.setAttribute("messageType", "error");
             request.setAttribute("token", token); // Preservar el token
             // No preservar el password
             // Reenviar de vuelta al formulario de restablecimiento
             request.getRequestDispatcher("/resetPassword.jsp").forward(request, response); // <<-- AJUSTA LA RUTA DEL JSP
            return;
        }

        // Opcional: Añadir validación de complejidad de la contraseña (longitud mínima, caracteres, etc.)


        // 3. Llamar al servicio para restablecer la contraseña
        // El servicio validará el token DE NUEVO, hasheará la password y la actualizará, y marcará el token como usado.
        boolean success = usuarioService.resetPassword(token, newPassword);

        // 4. Procesar el resultado del restablecimiento
        if (success) {
            // Restablecimiento exitoso. Redirigir al usuario a la página de login con un mensaje de éxito.
            // Usamos sendRedirect para evitar que el usuario envíe el formulario de nuevo si recarga.
            response.sendRedirect(request.getContextPath() + "/login.jsp?resetSuccess=true"); // Redirigir a login con param

        } else {
            // Fallo en el restablecimiento (token inválido/usado en la segunda verificación, error DB, etc.)
            request.setAttribute("resetMessage", "No se pudo restablecer la contraseña. El enlace puede ser inválido o haber expirado.");
            request.setAttribute("messageType", "error");
             // Preservar el token
             request.setAttribute("token", token);
             // Reenviar de vuelta al formulario de restablecimiento para mostrar el error
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response); // <<-- AJUSTA LA RUTA DEL JSP
        }
    }
}

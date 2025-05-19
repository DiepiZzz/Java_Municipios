package Controllers;

import Model.Entities.Usuario;
import Model.Services.UsuarioService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/resetPassword")
public class ResetPasswordController extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        Usuario usuarioAsociado = usuarioService.validateRecoveryToken(token);

        if (usuarioAsociado != null) {
            request.setAttribute("token", token);
            request.setAttribute("usuario", usuarioAsociado);

            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);

        } else {
            request.setAttribute("resetMessage", "El enlace de restablecimiento de contraseña es inválido o ha expirado.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("resetMessage", "Token, nueva contraseña o confirmación están vacíos.");
            request.setAttribute("messageType", "error");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("resetMessage", "La nueva contraseña y la confirmación no coinciden.");
            request.setAttribute("messageType", "error");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        boolean success = usuarioService.resetPassword(token, newPassword);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?resetSuccess=true");

        } else {
            request.setAttribute("resetMessage", "No se pudo restablecer la contraseña. El enlace puede ser inválido o haber expirado.");
            request.setAttribute("messageType", "error");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
        }
    }
}

package Controllers;

import Model.Services.UsuarioService;
import Model.Entities.PasswordRecoveryToken;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/forgotPassword")
public class ForgotPasswordController extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>>>>>> DEBUG: Entrando a doGet de ForgotPasswordController <<<<<<<");
        request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>>>>>> DEBUG: Entrando a doPost de ForgotPasswordController <<<<<<<");
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("recoveryMessage", "Por favor, ingrese una dirección de email.");
            request.setAttribute("messageType", "error");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
            return;
        }

        PasswordRecoveryToken generatedToken = usuarioService.requestPasswordRecovery(email);

        request.setAttribute("recoveryMessage", "Si la dirección de email está registrada, se ha enviado un enlace para restablecer tu contraseña.");
        request.setAttribute("messageType", "success");

        request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
    }
}

package Controllers;

import Model.Entities.Usuario;
import Model.Services.UsuarioService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class UsuarioController extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService();
        System.out.println("UsuarioController initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doPost de UsuarioController <<<<<<<");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("DEBUG UsuarioController Login: Campos vacíos.");
            request.setAttribute("errorMessage", "Por favor, ingrese usuario y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        Usuario usuarioAutenticado = usuarioService.autenticar(username, password);

        if (usuarioAutenticado != null) {
            System.out.println("DEBUG UsuarioController Login: Autenticación exitosa para usuario: " + username);

            HttpSession session = request.getSession(true);

            session.setAttribute("loggedInUser", usuarioAutenticado);

            session.setMaxInactiveInterval(30 * 60);

            System.out.println("DEBUG UsuarioController Login: Sesión creada/obtenida. ID: " + session.getId());
            System.out.println("DEBUG UsuarioController Login: Atributo 'loggedInUser' en sesión: " + session.getAttribute("loggedInUser"));
            System.out.println("DEBUG UsuarioController Login: ¿Es nueva sesión? " + session.isNew());

            response.sendRedirect(request.getContextPath() + "/home");
            System.out.println("DEBUG UsuarioController Login: Redirigiendo a /home.");

        } else {
            System.out.println("DEBUG UsuarioController Login: Autenticación fallida para usuario: " + username);

            request.setAttribute("errorMessage", "Usuario o contraseña incorrectos.");

            request.getRequestDispatcher("/login.jsp").forward(request, response);
            System.out.println("DEBUG UsuarioController Login: Reenviando a login.jsp con error.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>>>>>> DEBUG: Entrando a doGet de UsuarioController <<<<<<<");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}

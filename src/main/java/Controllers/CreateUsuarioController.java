package Controllers;

import Model.Entities.Usuario;
import Model.Services.UsuarioService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Signup")
public class CreateUsuarioController extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            nombre == null || nombre.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {

            request.setAttribute("creationMessage", "Todos los campos son obligatorios.");
            request.setAttribute("messageType", "error");
            request.setAttribute("username", username);
            request.setAttribute("nombre", nombre);
            request.setAttribute("email", email);

            request.getRequestDispatcher("/Signup.jsp").forward(request, response);
            return;
        }

        Usuario nuevoUsuario = new Usuario(username, password, nombre, email);

        boolean success = usuarioService.crearUsuario(nuevoUsuario);

        if (success) {
            request.setAttribute("creationMessage", "Usuario '" + username + "' creado con éxito. Ahora puedes iniciar sesión.");
            request.setAttribute("messageType", "success");

            request.getRequestDispatcher("/Signup.jsp").forward(request, response);

        } else {
            request.setAttribute("creationMessage", "El nombre de usuario '" + username + "' ya existe. Por favor, elija otro.");
            request.setAttribute("messageType", "error");
            request.setAttribute("username", username);
            request.setAttribute("nombre", nombre);
            request.setAttribute("email", email);

            request.getRequestDispatcher("/Signup.jsp").forward(request, response);
        }
    }
}


package Controllers;

import Model.Entities.Usuario;
import Model.Services.UsuarioService;


import java.io.IOException;
// Usar javax.servlet.* para compatibilidad con JPA 2.2 y Java EE 7/8
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Necesario para manejar la sesión del usuario

/**
 * Servlet que maneja las peticiones de inicio de sesión para los usuarios.
 * Mapeado a la URL /login.
 */
@WebServlet("/login") // Mapea este Servlet a la URL "/login"
public class UsuarioController extends HttpServlet { // Cambiado el nombre de la clase a UsuarioController

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Instanciar el servicio cuando se inicializa el Servlet
        this.usuarioService = new UsuarioService();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Recibe los datos del formulario de login.jsp (método POST)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener las credenciales del formulario
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validar que los parámetros no sean nulos o vacíos (validación básica)
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            // Si falta alguno, establecer un mensaje de error y volver a la página de login
            request.setAttribute("errorMessage", "Por favor, ingrese usuario y contraseña.");
            // Reenviar a la página de login (manteniendo el error message en el request)
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return; // Terminar la ejecución del método
        }

        // 2. Llamar al servicio para autenticar al usuario
        Usuario usuarioAutenticado = usuarioService.autenticar(username, password);

        // 3. Procesar el resultado de la autenticación
        if (usuarioAutenticado != null) {
            // Autenticación exitosa

            // Crear o obtener la sesión del usuario
            HttpSession session = request.getSession(true); // true crea la sesión si no existe
            session.setAttribute("usuario", usuarioAutenticado); // Guardar el usuario en la sesión

            // Redirigir a la página de inicio (home)
            // Usa sendRedirect para generar una nueva petición GET
            // request.getContextPath() añade el nombre de la aplicación si no está en la raíz
            response.sendRedirect(request.getContextPath() + "/home.jsp"); // Asegúrate de crear home.jsp

        } else {
            // Autenticación fallida

            // Establecer un mensaje de error en el request para mostrar en la vista
            request.setAttribute("errorMessage", "Usuario o contraseña incorrectos.");

            // Reenviar la solicitud de vuelta a la página de login
            // Usa forward para mantener la misma solicitud y los atributos del request
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    // Si quieres que al acceder a /login con GET se muestre la página de login, puedes añadir un doGet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Simplemente reenviar a la página de login para mostrar el formulario inicial
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

}

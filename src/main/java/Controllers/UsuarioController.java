package Controllers; // Asegura que este es el nombre correcto de tu paquete de controladores

import Model.Entities.Usuario; // Importa la entidad Usuario
import Model.Services.UsuarioService; // Importa el servicio de Usuario

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
public class UsuarioController extends HttpServlet { // Nombre de la clase: UsuarioController

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Instanciar el servicio cuando se inicializa el Servlet
        this.usuarioService = new UsuarioService();
        System.out.println("UsuarioController initialized."); // Log de inicialización
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Recibe los datos del formulario de login.jsp (método POST)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doPost de UsuarioController <<<<<<<"); // Log de entrada

        // 1. Obtener las credenciales del formulario
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validar que los parámetros no sean nulos o vacíos (validación básica)
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("DEBUG UsuarioController Login: Campos vacíos."); // Log
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
            System.out.println("DEBUG UsuarioController Login: Autenticación exitosa para usuario: " + username); // Log

            // Crear o obtener la sesión del usuario
            HttpSession session = request.getSession(true); // true crea la sesión si no existe

            // *** ¡CORRECCIÓN AQUÍ! GUARDAR EL USUARIO CON EL NOMBRE "loggedInUser" ***
            session.setAttribute("loggedInUser", usuarioAutenticado); // <<-- Nombre del atributo corregido

            session.setMaxInactiveInterval(30 * 60); // Opcional: configurar tiempo de expiración de sesión (ej. 30 minutos)

            // *** Logs adicionales para verificar la sesión ***
            System.out.println("DEBUG UsuarioController Login: Sesión creada/obtenida. ID: " + session.getId());
            System.out.println("DEBUG UsuarioController Login: Atributo 'loggedInUser' en sesión: " + session.getAttribute("loggedInUser"));
            System.out.println("DEBUG UsuarioController Login: ¿Es nueva sesión? " + session.isNew());
            // ***********************************************


            // *** ¡CORRECCIÓN AQUÍ! REDIRIGIR AL SERVLET /home, NO AL JSP ***
            // Redirigir a la página principal (Home) -- Usa sendRedirect para generar una nueva petición GET
            // request.getContextPath() añade el nombre de la aplicación si no está en la raíz
            response.sendRedirect(request.getContextPath() + "/home"); // <<-- REDIRIGIR A LA URL DEL HOMECONTROLLER
            System.out.println("DEBUG UsuarioController Login: Redirigiendo a /home."); // Log


        } else {
            // Autenticación fallida
            System.out.println("DEBUG UsuarioController Login: Autenticación fallida para usuario: " + username); // Log

            // Establecer un mensaje de error en el request para mostrar en la vista
            request.setAttribute("errorMessage", "Usuario o contraseña incorrectos.");

            // Reenviar la solicitud de vuelta a la página de login
            // Usa forward para mantener la misma solicitud y los atributos del request
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            System.out.println("DEBUG UsuarioController Login: Reenviando a login.jsp con error."); // Log
        }
    }

    // Si quieres que al acceder a /login con GET se muestre la página de login, puedes añadir un doGet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>>>>>> DEBUG: Entrando a doGet de UsuarioController <<<<<<<"); // Log
        // Simplemente reenviar a la página de login para mostrar el formulario inicial
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // Opcional: método destroy ...
    // @Override
    // public void destroy() {
    //     if (usuarioService != null) {
            // Si implementaste closeFactories en el servicio
            // usuarioService.closeFactories();
    //     }
    //     super.destroy();
    // }
}

// Asegúrate de que este es el nombre del paquete correcto en tu estructura
package Controllers;

 // Importa tu entidad Usuario

import Model.Entities.Usuario;
import Model.Services.UsuarioService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet que maneja la vista y el procesamiento de la creación de nuevos usuarios.
 * Mapeado a la URL /createUser.
 */
@WebServlet("/Signup") // Mapea este Servlet a la URL "/createUser"
public class CreateUsuarioController extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Simplemente muestra la página de creación de usuario.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Reenviar a la página JSP del formulario de creación
        request.getRequestDispatcher("/Signup.jsp").forward(request, response);
    }


    /**
     * Handles the HTTP <code>POST</code> method.
     * Recibe los datos del formulario de creación de usuario de createUser.jsp
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener los datos del formulario
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");

         // 2. Validar datos de entrada (validación básica)
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            nombre == null || nombre.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {

            request.setAttribute("creationMessage", "Todos los campos son obligatorios.");
            request.setAttribute("messageType", "error");
            // Preservar los valores ingresados (excepto password) para que el usuario no los pierda
            request.setAttribute("username", username);
            request.setAttribute("nombre", nombre);
            request.setAttribute("email", email);

            // Reenviar de vuelta a la página de creación para mostrar el error
            request.getRequestDispatcher("/Signup.jsp").forward(request, response);
            return; // Terminar la ejecución del método
        }


        // 3. Crear el objeto Usuario
        Usuario nuevoUsuario = new Usuario(username, password, nombre, email);

        // 4. Llamar al servicio para intentar crear el usuario
        boolean success = usuarioService.crearUsuario(nuevoUsuario);

        // 5. Procesar el resultado de la creación
        if (success) {
            // Creación exitosa
            // Establecer un mensaje de éxito
            request.setAttribute("creationMessage", "Usuario '" + username + "' creado con éxito. Ahora puedes iniciar sesión.");
            request.setAttribute("messageType", "success");

            // Opcional: Podrías redirigir a la página de login con un parámetro de éxito
            // response.sendRedirect(request.getContextPath() + "/login.jsp?creationSuccess=true");
            // Pero reenviar a la misma página para mostrar el mensaje de éxito también es una opción
             request.getRequestDispatcher("/Signup.jsp").forward(request, response);


        } else {
            // Creación fallida (username ya existe o error general)
            // El servicio ya imprime en consola si es por username existente
            request.setAttribute("creationMessage", "El nombre de usuario '" + username + "' ya existe. Por favor, elija otro.");
            request.setAttribute("messageType", "error");
            // Preservar los valores ingresados (excepto password) si falla
            request.setAttribute("username", username);
            request.setAttribute("nombre", nombre);
            request.setAttribute("email", email);

             // Reenviar de vuelta a la página de creación para mostrar el error
            request.getRequestDispatcher("/Signup.jsp").forward(request, response);
        }
    }

}

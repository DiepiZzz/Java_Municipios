// Asegúrate de que este es el nombre del paquete correcto en tu estructura
package Controllers;

import Model.Services.UsuarioService; // Importar tu servicio de Usuario
import Model.Entities.PasswordRecoveryToken; // Importar la entidad token (si el servicio la retorna, aunque no la usamos directamente aquí)

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet que maneja la vista y el procesamiento de la solicitud de recuperación de contraseña (ingresar email).
 * Mapeado a la URL /forgotPassword.
 */
@WebServlet("/forgotPassword") // <<-- Mapea este Servlet a la URL "/forgotPassword"
public class ForgotPasswordController extends HttpServlet {

    private UsuarioService usuarioService; // Usamos el mismo servicio de usuario

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService(); // Instanciar el servicio
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Muestra la página con el formulario para ingresar el email.
     * Acceder a /forgotPassword con GET te llevará a forgotPassword.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Reenviar a la página JSP del formulario de email
          System.out.println(">>>>>>> DEBUG: Entrando a doGet de ForgotPasswordController <<<<<<<"); // <<-- AÑADE ESTA LÍNEA
        request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
    }


    /**
     * Handles the HTTP <code>POST</code> method.
     * Recibe el email ingresado por el usuario del formulario en forgotPassword.jsp.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 System.out.println(">>>>>>> DEBUG: Entrando a doPost de ForgotPasswordController <<<<<<<"); // <<-- AÑADE ESTA LÍNEA EXACTAMENTE AQUÍ
        // 1. Obtener el email del formulario
        String email = request.getParameter("email");

         // 2. Validar que el email no esté vacío
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("recoveryMessage", "Por favor, ingrese una dirección de email.");
            request.setAttribute("messageType", "error");
            // Preservar el email ingresado
            request.setAttribute("email", email);
            // Reenviar de vuelta a la página para mostrar el error
            request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
            return; // Terminar
        }

        // 3. Llamar al servicio para procesar la solicitud.
        // El servicio buscará el usuario, generará el token, lo guardará y CONSTRUIRÁ Y ENVIARÁ EL EMAIL.
        // El retorno del servicio (PasswordRecoveryToken o null) nos sirve para saber si el proceso interno
        // (buscar usuario, guardar token, enviar email) tuvo éxito.
        PasswordRecoveryToken generatedToken = usuarioService.requestPasswordRecovery(email); // Este método DEBE encargarse de enviar el email

        // 4. Procesar el resultado (para mostrar un mensaje al usuario).
        // POR SEGURIDAD, el mensaje al usuario debe ser genérico para no confirmar si el email existe o no.
        // El mensaje es el mismo si el servicio retorna null (email no encontrado o fallo en guardar/enviar)
        // o si retorna un token (email encontrado y proceso iniciado).

        // Establecer un mensaje genérico de éxito (o fallo técnico interno)
        request.setAttribute("recoveryMessage", "Si la dirección de email está registrada, se ha enviado un enlace para restablecer tu contraseña.");
        request.setAttribute("messageType", "success"); // Tratamos la respuesta como un "éxito" de proceso para el usuario

        // Nota: Si quieres manejar errores específicos de envío de email en el servicio,
        // el servicio debería lanzar una excepción distinta o retornar un código de error.
        // Pero el mensaje al USUARIO FINAL debe seguir siendo genérico por seguridad.

        // No preservar el email ingresado en caso de "éxito" genérico
        // request.setAttribute("email", ""); // Opcional: limpiar el campo


         // Siempre reenviar de vuelta a la página para mostrar el mensaje
        request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
    }
}
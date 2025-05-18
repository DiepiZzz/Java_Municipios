package Controllers; // Asegura que este es el nombre correcto de tu paquete de controladores

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Para manejar la sesión

/**
 * Servlet que maneja el cierre de sesión del usuario.
 * Invalida la sesión HTTP y redirige a la página de login.
 * Mapeado a la URL /logout.
 */
@WebServlet("/logout") // <<-- Mapea este Servlet a la URL "/logout"
public class LogoutController extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     * El cierre de sesión se suele manejar con GET por simplicidad (aunque POST sería más RESTful).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doGet de LogoutController <<<<<<<"); // Log de entrada

        // 1. Obtener la sesión actual (no crear una si no existe)
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 2. Invalidar la sesión
            session.invalidate();
            System.out.println("DEBUG Logout: Sesión invalidada.");
        } else {
             System.out.println("DEBUG Logout: Intento de logout sin sesión activa.");
        }

        // 3. Redirigir a la página de login
        // Usar sendRedirect para que el navegador haga una nueva petición GET a la página de login
        response.sendRedirect(request.getContextPath() + "/login.jsp"); // Ajusta la URL de tu página de login
        System.out.println("DEBUG Logout: Redirigiendo a login.jsp.");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * También puedes manejar el logout con POST si prefieres.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Simplemente llama a doGet para manejar el logout
    }
}

package Controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doGet de LogoutController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
            System.out.println("DEBUG Logout: Sesión invalidada.");
        } else {
            System.out.println("DEBUG Logout: Intento de logout sin sesión activa.");
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp");
        System.out.println("DEBUG Logout: Redirigiendo a login.jsp.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

package Controllers;

import Model.Entities.Municipio;
import Model.Services.MunicipioService;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.municipioService = new MunicipioService();
        System.out.println("HomeController initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entering HomeController doGet <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG Home: User not authenticated. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("DEBUG Home: User authenticated. Loading municipalities.");

        List<Municipio> municipios = municipioService.obtenerTodosLosMunicipios();

        request.setAttribute("municipios", municipios);

        System.out.println("DEBUG Home: Forwarding to home.jsp with municipality list.");
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

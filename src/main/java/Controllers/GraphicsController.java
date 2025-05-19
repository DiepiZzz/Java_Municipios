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

@WebServlet("/graphics")
public class GraphicsController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.municipioService = new MunicipioService();
        System.out.println("GraphicsController initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doGet de GraphicsController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG Graphics: Usuario no autenticado. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("DEBUG Graphics: Usuario autenticado. Cargando municipios para gráficos.");

        List<Municipio> municipios = municipioService.obtenerTodosLosMunicipios();

        request.setAttribute("municipios", municipios);

        System.out.println("DEBUG Graphics: Reenviando a graphics.jsp con lista de municipios.");
        request.getRequestDispatcher("/graphics.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

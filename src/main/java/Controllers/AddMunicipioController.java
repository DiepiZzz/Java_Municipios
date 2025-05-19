package Controllers;

import Model.Entities.Municipio;
import Model.Services.MunicipioService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/addMunicipio")
public class AddMunicipioController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.municipioService = new MunicipioService();
        System.out.println("AddMunicipioController initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doGet de AddMunicipioController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG AddMunicipio: Usuario no autenticado. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("DEBUG AddMunicipio: Usuario autenticado. Mostrando formulario.");
        request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doPost de AddMunicipioController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG AddMunicipio: Usuario no autenticado en POST. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String nombre = request.getParameter("nombre");
        String departamento = request.getParameter("departamento");
        String pais = request.getParameter("pais");
        String alcalde = request.getParameter("alcalde");
        String gobernador = request.getParameter("gobernador");
        String patronoReligioso = request.getParameter("patronoReligioso");
        String numHabitantesStr = request.getParameter("numHabitantes");
        String numCasasStr = request.getParameter("numCasas");
        String numParquesStr = request.getParameter("numParques");
        String numColegiosStr = request.getParameter("numColegios");
        String descripcion = request.getParameter("descripcion");

        if (nombre == null || nombre.trim().isEmpty() ||
            departamento == null || departamento.trim().isEmpty() ||
            pais == null || pais.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Los campos Nombre, Departamento y País son obligatorios.");
            request.setAttribute("nombre", nombre);
            request.setAttribute("departamento", departamento);
            request.setAttribute("pais", pais);
            request.setAttribute("alcalde", alcalde);
            request.setAttribute("gobernador", gobernador);
            request.setAttribute("patronoReligioso", patronoReligioso);
            request.setAttribute("descripcion", descripcion);

            System.out.println("DEBUG AddMunicipio: Validación fallida. Campos obligatorios vacíos.");
            request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
            return;
        }

        Integer numHabitantes = null;
        Integer numCasas = null;
        Integer numParques = null;
        Integer numColegios = null;

        try {
            if (numHabitantesStr != null && !numHabitantesStr.trim().isEmpty()) {
                numHabitantes = Integer.parseInt(numHabitantesStr);
            }
            if (numCasasStr != null && !numCasasStr.trim().isEmpty()) {
                numCasas = Integer.parseInt(numCasasStr);
            }
            if (numParquesStr != null && !numParquesStr.trim().isEmpty()) {
                numParques = Integer.parseInt(numParquesStr);
            }
            if (numColegiosStr != null && !numColegiosStr.trim().isEmpty()) {
                numColegios = Integer.parseInt(numColegiosStr);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Los campos numéricos deben contener solo números enteros.");
            request.setAttribute("nombre", nombre);
            request.setAttribute("departamento", departamento);
            request.setAttribute("pais", pais);
            request.setAttribute("alcalde", alcalde);
            request.setAttribute("gobernador", gobernador);
            request.setAttribute("patronoReligioso", patronoReligioso);
            request.setAttribute("numHabitantes", numHabitantesStr);
            request.setAttribute("numCasas", numCasasStr);
            request.setAttribute("numParques", numParquesStr);
            request.setAttribute("numColegios", numColegiosStr);
            request.setAttribute("descripcion", descripcion);

            System.out.println("DEBUG AddMunicipio: Validación fallida. Error de formato numérico.");
            request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
            return;
        }

        Municipio nuevoMunicipio = new Municipio(
                nombre,
                departamento,
                pais,
                alcalde,
                gobernador,
                patronoReligioso,
                numHabitantes,
                numCasas,
                numParques,
                numColegios,
                descripcion
        );

        try {
            municipioService.crearMunicipio(nuevoMunicipio);
            System.out.println("DEBUG AddMunicipio: Municipio guardado con éxito: " + nombre);

            response.sendRedirect(request.getContextPath() + "/home?message=Municipio agregado con éxito&messageType=success");
            System.out.println("DEBUG AddMunicipio: Redirigiendo a /home.");

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error al guardar el municipio: " + e.getMessage());
            request.setAttribute("nombre", nombre);
            request.setAttribute("departamento", departamento);
            request.setAttribute("pais", pais);
            request.setAttribute("alcalde", alcalde);
            request.setAttribute("gobernador", gobernador);
            request.setAttribute("patronoReligioso", patronoReligioso);
            request.setAttribute("numHabitantes", numHabitantesStr);
            request.setAttribute("numCasas", numCasasStr);
            request.setAttribute("numParques", numParquesStr);
            request.setAttribute("numColegios", numColegiosStr);
            request.setAttribute("descripcion", descripcion);

            System.err.println("DEBUG AddMunicipio: Error al guardar el municipio: " + e.getMessage());
            e.printStackTrace();
            request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
        }
    }
}

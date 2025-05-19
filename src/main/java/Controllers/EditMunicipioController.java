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

@WebServlet("/editMunicipio")
public class EditMunicipioController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.municipioService = new MunicipioService();
        System.out.println("EditMunicipioController initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doGet de EditMunicipioController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG EditMunicipio GET: Usuario no autenticado. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String municipioIdStr = request.getParameter("id");
        Integer municipioId = null;

        if (municipioIdStr != null && !municipioIdStr.trim().isEmpty()) {
            try {
                municipioId = Integer.parseInt(municipioIdStr);
                System.out.println("DEBUG EditMunicipio GET: Recibido ID de municipio: " + municipioId);
            } catch (NumberFormatException e) {
                System.err.println("DEBUG EditMunicipio GET: ID de municipio inválido: " + municipioIdStr);
                response.sendRedirect(request.getContextPath() + "/home?message=ID de municipio inv%C3%A1lido&messageType=error");
                return;
            }
        } else {
            System.err.println("DEBUG EditMunicipio GET: No se proporcionó ID de municipio.");
            response.sendRedirect(request.getContextPath() + "/home?message=No se especific%C3%B3 el municipio a editar&messageType=error");
            return;
        }

        Municipio municipioToEdit = municipioService.obtenerMunicipioPorId(municipioId);

        if (municipioToEdit != null) {
            request.setAttribute("municipio", municipioToEdit);
            System.out.println("DEBUG EditMunicipio GET: Municipio encontrado y cargado para edición: " + municipioToEdit.getNombre());

            request.getRequestDispatcher("/editMunicipio.jsp").forward(request, response);
        } else {
            System.err.println("DEBUG EditMunicipio GET: Municipio con ID " + municipioId + " no encontrado.");
            response.sendRedirect(request.getContextPath() + "/home?message=Municipio no encontrado&messageType=error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doPost de EditMunicipioController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG EditMunicipio POST: Usuario no autenticado. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String municipioIdStr = request.getParameter("id");
        Integer municipioId = null;

         if (municipioIdStr != null && !municipioIdStr.trim().isEmpty()) {
            try {
                municipioId = Integer.parseInt(municipioIdStr);
                System.out.println("DEBUG EditMunicipio POST: Recibido ID de municipio: " + municipioId);
            } catch (NumberFormatException e) {
                System.err.println("DEBUG EditMunicipio POST: ID de municipio inválido en POST: " + municipioIdStr);
                response.sendRedirect(request.getContextPath() + "/home?message=Error al procesar la edici%C3%B3n: ID inv%C3%A1lido&messageType=error");
                return;
            }
        } else {
            System.err.println("DEBUG EditMunicipio POST: No se proporcionó ID de municipio en POST.");
            response.sendRedirect(request.getContextPath() + "/home?message=Error al procesar la edici%C3%B3n: No se especific%C3%B3 el municipio&messageType=error");
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
            request.setAttribute("municipio", new Municipio(nombre, departamento, pais, alcalde, gobernador, patronoReligioso, null, null, null, null, descripcion));
            request.setAttribute("municipioId", municipioId);

            System.out.println("DEBUG EditMunicipio POST: Validación fallida. Campos obligatorios vacíos.");
            request.getRequestDispatcher("/editMunicipio.jsp").forward(request, response);
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
            request.setAttribute("municipio", new Municipio(nombre, departamento, pais, alcalde, gobernador, patronoReligioso, numHabitantes, numCasas, numParques, numColegios, descripcion));
            request.setAttribute("municipioId", municipioId);

            System.out.println("DEBUG EditMunicipio POST: Validación fallida. Error de formato numérico.");
            request.getRequestDispatcher("/editMunicipio.jsp").forward(request, response);
            return;
        }


        Municipio municipioToUpdate = municipioService.obtenerMunicipioPorId(municipioId);

        if (municipioToUpdate != null) {
            municipioToUpdate.setNombre(nombre);
            municipioToUpdate.setDepartamento(departamento);
            municipioToUpdate.setPais(pais);
            municipioToUpdate.setAlcalde(alcalde);
            municipioToUpdate.setGobernador(gobernador);
            municipioToUpdate.setPatronoReligioso(patronoReligioso);
            municipioToUpdate.setNumHabitantes(numHabitantes);
            municipioToUpdate.setNumCasas(numCasas);
            municipioToUpdate.setNumParques(numParques);
            municipioToUpdate.setNumColegios(numColegios);
            municipioToUpdate.setDescripcion(descripcion);

            try {
                municipioService.actualizarMunicipio(municipioToUpdate);
                System.out.println("DEBUG EditMunicipio POST: Municipio actualizado con éxito: " + nombre);

                response.sendRedirect(request.getContextPath() + "/home?message=Municipio actualizado con éxito&messageType=success");
                System.out.println("DEBUG EditMunicipio POST: Redirigiendo a /home.");

            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error al actualizar el municipio: " + e.getMessage());
                request.setAttribute("municipio", municipioToUpdate);
                request.setAttribute("municipioId", municipioId);

                System.err.println("DEBUG EditMunicipio POST: Error al actualizar el municipio: " + e.getMessage());
                e.printStackTrace();
                request.getRequestDispatcher("/editMunicipio.jsp").forward(request, response);
            }
        } else {
            System.err.println("DEBUG EditMunicipio POST: Municipio con ID " + municipioId + " no encontrado para actualizar.");
            response.sendRedirect(request.getContextPath() + "/home?message=Error al actualizar: Municipio no encontrado&messageType=error");
        }
    }
}

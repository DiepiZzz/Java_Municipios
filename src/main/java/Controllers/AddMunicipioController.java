package Controllers; // Asegura que este es el nombre correcto de tu paquete de controladores

import Model.Entities.Municipio; // Importar la entidad Municipio
import Model.Services.MunicipioService; // Importar el servicio de Municipio

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Para verificar la sesión

/**
 * Servlet que maneja la vista y el procesamiento del formulario para agregar un nuevo municipio.
 * Mapeado a la URL /addMunicipio.
 */
@WebServlet("/addMunicipio") // <<-- Mapea este Servlet a la URL "/addMunicipio"
public class AddMunicipioController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.municipioService = new MunicipioService(); // Instanciar el servicio de municipios
        System.out.println("AddMunicipioController initialized."); // Log de inicialización
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Muestra la página con el formulario para agregar un municipio.
     * Requiere que el usuario esté autenticado.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doGet de AddMunicipioController <<<<<<<"); // Log de entrada

        // *** 1. Verificar si el usuario está autenticado ***
        HttpSession session = request.getSession(false); // No crear una nueva sesión si no existe

        if (session == null || session.getAttribute("loggedInUser") == null) {
            // Si no hay sesión o no hay usuario autenticado en sesión, redirigir a la página de login
            System.out.println("DEBUG AddMunicipio: Usuario no autenticado. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Ajusta la URL de tu página de login
            return; // Detener el procesamiento
        }

        // Si el usuario está autenticado, mostrar el formulario
        System.out.println("DEBUG AddMunicipio: Usuario autenticado. Mostrando formulario.");
        request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Recibe los datos del formulario, crea un nuevo municipio y lo guarda.
     * Requiere que el usuario esté autenticado.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doPost de AddMunicipioController <<<<<<<"); // Log de entrada

        // *** 1. Verificar si el usuario está autenticado ***
        HttpSession session = request.getSession(false); // No crear una nueva sesión si no existe

        if (session == null || session.getAttribute("loggedInUser") == null) {
            // Si no hay sesión o no hay usuario autenticado en sesión, redirigir a la página de login
            System.out.println("DEBUG AddMunicipio: Usuario no autenticado en POST. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Ajusta la URL de tu página de login
            return; // Detener el procesamiento
        }

        // 2. Obtener los parámetros del formulario
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

        // 3. Validar los parámetros (ej. campos requeridos no vacíos)
        if (nombre == null || nombre.trim().isEmpty() ||
            departamento == null || departamento.trim().isEmpty() ||
            pais == null || pais.trim().isEmpty()) {

            // Si hay campos requeridos vacíos, establecer mensaje de error y reenviar al formulario
            request.setAttribute("errorMessage", "Los campos Nombre, Departamento y País son obligatorios.");
            // Preservar los valores ingresados (excepto números si la conversión falla)
            request.setAttribute("nombre", nombre);
            request.setAttribute("departamento", departamento);
            request.setAttribute("pais", pais);
            request.setAttribute("alcalde", alcalde);
            request.setAttribute("gobernador", gobernador);
            request.setAttribute("patronoReligioso", patronoReligioso);
            request.setAttribute("descripcion", descripcion);
            // No preservar números si la conversión a Integer falla

            System.out.println("DEBUG AddMunicipio: Validación fallida. Campos obligatorios vacíos.");
            request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
            return; // Detener el procesamiento
        }

        // 4. Convertir campos numéricos de String a Integer
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
            // Si la conversión falla, establecer mensaje de error y reenviar al formulario
            request.setAttribute("errorMessage", "Los campos numéricos deben contener solo números enteros.");
             // Preservar los valores ingresados (incluyendo los strings numéricos originales si quieres)
            request.setAttribute("nombre", nombre);
            request.setAttribute("departamento", departamento);
            request.setAttribute("pais", pais);
            request.setAttribute("alcalde", alcalde);
            request.setAttribute("gobernador", gobernador);
            request.setAttribute("patronoReligioso", patronoReligioso);
            request.setAttribute("numHabitantes", numHabitantesStr); // Preservar el string original
            request.setAttribute("numCasas", numCasasStr);
            request.setAttribute("numParques", numParquesStr);
            request.setAttribute("numColegios", numColegiosStr);
            request.setAttribute("descripcion", descripcion);

            System.out.println("DEBUG AddMunicipio: Validación fallida. Error de formato numérico.");
            request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
            return; // Detener el procesamiento
        }


        // 5. Crear un nuevo objeto Municipio
        Municipio nuevoMunicipio = new Municipio(
                nombre,
                departamento,
                pais,
                alcalde,
                gobernador,
                patronoReligioso,
                numHabitantes, // Ya son Integer (o null)
                numCasas,
                numParques,
                numColegios,
                descripcion
        );

        // 6. Guardar el municipio usando el servicio
        try {
            municipioService.crearMunicipio(nuevoMunicipio);
            System.out.println("DEBUG AddMunicipio: Municipio guardado con éxito: " + nombre);

            // 7. Redirigir a la página principal (Home) después de guardar
            // Opcional: Puedes añadir un parámetro a la URL para mostrar un mensaje de éxito en Home
            response.sendRedirect(request.getContextPath() + "/home?message=Municipio agregado con éxito&messageType=success");
            System.out.println("DEBUG AddMunicipio: Redirigiendo a /home.");

        } catch (Exception e) {
            // Si ocurre un error al guardar (ej. error de DB, nombre duplicado si la DB lo restringe)
            request.setAttribute("errorMessage", "Error al guardar el municipio: " + e.getMessage());
             // Preservar todos los valores ingresados en caso de error
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
            // Reenviar de vuelta al formulario con el mensaje de error
            request.getRequestDispatcher("/addMunicipio.jsp").forward(request, response);
        }
    }

    // Opcional: Método para cerrar el EntityManagerFactory si es necesario al finalizar el Servlet
    // @Override
    // public void destroy() {
    //     if (municipioService != null) {
    //         // Asumiendo que tu MunicipioService tiene un método para cerrar su repositorio's EMF
    //         // municipioService.closeFactory();
    //     }
    //     super.destroy();
    // }
}

package Controllers; // Ensure this is the correct package name in your project structure

import Model.Entities.Municipio; // Import the Municipio entity
import Model.Services.MunicipioService; // Import the Municipio service

import java.io.IOException;
import java.util.List; // For the list of municipalities
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // To handle the session

/**
 * Servlet that handles the main view (Home) after login.
 * Displays the list of municipalities and provides links to other functionalities.
 * Mapped to the URL /home.
 */
@WebServlet("/home") // <-- Maps this Servlet to the "/home" URL
public class HomeController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Instantiate the municipality service
        this.municipioService = new MunicipioService();
        System.out.println("HomeController initialized."); // Initialization log
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the main page with the municipality table.
     * Requires the user to be authenticated.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entering HomeController doGet <<<<<<<"); // Entry log

        // *** 1. Verify if the user is authenticated ***
        // Do not create a new session if one does not exist
        HttpSession session = request.getSession(false);

        // If there is no session or no authenticated user in the session, redirect to the login page
        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG Home: User not authenticated. Redirecting to login.");
            // Adjust the URL of your login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return; // Stop processing
        }

        // If the user is authenticated, proceed to load the data
        System.out.println("DEBUG Home: User authenticated. Loading municipalities.");

        // 2. Get the list of municipalities from the service
        List<Municipio> municipios = municipioService.obtenerTodosLosMunicipios(); // Use the method from your service

        // 3. Put the list of municipalities in the request so the JSP can use it
        request.setAttribute("municipios", municipios);

        // 4. Forward to the main JSP page (home.jsp)
        System.out.println("DEBUG Home: Forwarding to home.jsp with municipality list.");
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * This Servlet does not handle POST directly from the main page,
     * but you can add logic if necessary (e.g., filters, search).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // For now, simply redirect to GET to display the page
        doGet(request, response);
    }

    // Optional: Method to close the EntityManagerFactory if necessary when the Servlet is destroyed
    // @Override
    // public void destroy() {
    //     if (municipioService != null) {
    //         // Assuming your MunicipioService has a method to close its repository's EMF
    //         // municipioService.closeFactory();
    //     }
    //     super.destroy();
    // }
}
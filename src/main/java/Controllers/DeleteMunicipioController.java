package Controllers;

import Model.Services.MunicipioService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/deleteMunicipio")
public class DeleteMunicipioController extends HttpServlet {

    private MunicipioService municipioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.municipioService = new MunicipioService();
        System.out.println("DeleteMunicipioController initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>>>>>> DEBUG: Entrando a doPost de DeleteMunicipioController <<<<<<<");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            System.out.println("DEBUG DeleteMunicipio: Usuario no autenticado. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String municipioIdStr = request.getParameter("id");
        Integer municipioId = null;

        if (municipioIdStr != null && !municipioIdStr.trim().isEmpty()) {
            try {
                municipioId = Integer.parseInt(municipioIdStr);
                System.out.println("DEBUG DeleteMunicipio: Recibido ID de municipio para eliminar: " + municipioId);
            } catch (NumberFormatException e) {
                System.err.println("DEBUG DeleteMunicipio: ID de municipio inválido para eliminar: " + municipioIdStr);
                response.sendRedirect(request.getContextPath() + "/home?message=ID de municipio inv%C3%A1lido para eliminar&messageType=error");
                return;
            }
        } else {
            System.err.println("DEBUG DeleteMunicipio: No se proporcionó ID de municipio para eliminar.");
            response.sendRedirect(request.getContextPath() + "/home?message=No se especific%C3%B3 el municipio a eliminar&messageType=error");
            return;
        }

        try {
            municipioService.eliminarMunicipio(municipioId);
            System.out.println("DEBUG DeleteMunicipio: Municipio con ID " + municipioId + " eliminado con éxito.");

            response.sendRedirect(request.getContextPath() + "/home?message=Municipio eliminado con éxito&messageType=success");
            System.out.println("DEBUG DeleteMunicipio: Redirigiendo a /home.");

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error al eliminar el municipio con ID " + municipioId + ": " + e.getMessage());
            System.err.println("DEBUG DeleteMunicipio: Error al eliminar el municipio con ID " + municipioId + ": " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home?message=Error al eliminar el municipio&messageType=error");
        }
    }
}

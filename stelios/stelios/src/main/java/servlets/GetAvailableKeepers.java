package servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import database.tables.EditPetKeepersTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mainClasses.PetKeeper;
import com.google.gson.Gson;
import java.util.List;


/**
 *
 * @author ioannis
 */
public class GetAvailableKeepers extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ClassNotFoundException {
        //
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        System.out.println("Opened GetAvailableKeepers");

        try (PrintWriter out = response.getWriter()) {
            EditPetKeepersTable eut = new EditPetKeepersTable();
            List<PetKeeper> petKeepers = eut.getAvailableKeepers("all"); // Always fetch available keepers
            String json = new Gson().toJson(petKeepers); // Convert the list to JSON
            out.println(json);
            response.setStatus(200);
        } catch (SQLException ex) {
            Logger.getLogger(GetAvailableKeepers.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Set error status code
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GetAvailableKeepers.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Set error status code
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

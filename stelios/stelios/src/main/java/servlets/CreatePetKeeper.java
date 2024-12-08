package servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import database.tables.EditPetKeepersTable;

import java.io.BufferedReader;
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

import javax.servlet.annotation.WebServlet;
import java.util.List;


@WebServlet(name = "CreatePetKeeper", urlPatterns = {"/CreatePetKeeper"})
public class CreatePetKeeper extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            System.out.println("Opened CreatePetKeeper");
            response.setContentType("application/json;charset=UTF-8");

            // Read from request
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (Exception e) {
                /* report an error */
            }

            String requestData = sb.toString();

            try (PrintWriter out = response.getWriter()) {
                EditPetKeepersTable eut = new EditPetKeepersTable();
                Gson gson = new Gson();

                // Convert JSON string to PetKeeper object
                PetKeeper newUser = gson.fromJson(requestData, PetKeeper.class);

                // Add the new user to the database
                eut.addNewPetKeeper(newUser);

                response.setStatus(200); // Set success status
                out.println(gson.toJson(newUser)); // Optionally return the added user as JSON
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CreatePetKeeper.class.getName()).log(Level.SEVERE, null, ex);
                response.setStatus(500); // Set error status code
                // Optionally write the error message to the response
            }
        }
}

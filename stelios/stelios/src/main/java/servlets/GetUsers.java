package servlets;

import database.tables.EditPetKeepersTable;
import database.tables.EditPetOwnersTable;
import mainClasses.PetKeeper;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainClasses.PetOwner;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ioannis
 * Servlet to get all users who are either catkeepers or dogkeepers.
 */
public class GetUsers extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String type=request.getParameter("type");

        try (PrintWriter out = response.getWriter()) {
            if(type.equals("keeper")) {
                EditPetKeepersTable eut = new EditPetKeepersTable();
                // Get all keepers
                ArrayList<PetKeeper> catKeepers = eut.getKeepers("both");
                // Convert the list to JSON
                String json = new Gson().toJson(catKeepers);
                out.println(json);

            } else if (type.equals("owner")) {

                EditPetOwnersTable eut2 = new EditPetOwnersTable();
                // Get all owners
                ArrayList<PetOwner> petOwners = eut2.getOwners();
                // Convert the list to JSON
                String json = new Gson().toJson(petOwners);
                out.println(json);
            }
            response.setStatus(200);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(GetUsers.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Set error status code
        }
    }

    // Other methods and class closing...
}

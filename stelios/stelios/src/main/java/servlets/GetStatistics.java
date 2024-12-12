package servlets;

import database.tables.EditPetOwnersTable;
import database.tables.EditPetKeepersTable;
import database.tables.EditBookingsTable;
import com.google.gson.Gson;
import database.tables.EditPetsTable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainClasses.Booking;
import mainClasses.Pet;
import mainClasses.PetKeeper;
import mainClasses.PetOwner;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class GetStatistics extends HttpServlet {
    // ... existing doGet method ...

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            EditPetsTable petsTable = new EditPetsTable();
            EditPetKeepersTable keepersTable = new EditPetKeepersTable();
            EditPetOwnersTable ownersTable = new EditPetOwnersTable();
            EditBookingsTable bookingTable = new EditBookingsTable();

            // Fetch the entire collections
            List<Pet> pets = petsTable.databaseToPets();
            List<PetKeeper> keepers = keepersTable.getKeepers("both");
            List<PetOwner> owners = ownersTable.getOwners();
            List<Booking> bookings = bookingTable.databaseToBookings("finished", "0", "0");

            // Create a combined data structure to send as JSON
            Map<String, Object> data = new HashMap<>();
            data.put("pets", pets);
            data.put("keepers", keepers);
            data.put("owners", owners);
            data.put("bookings", bookings);

            // Convert data to JSON
            Gson gson = new Gson();
            String json = gson.toJson(data);

            // Send the JSON as the response
            out.println(json);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(GetStatistics.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

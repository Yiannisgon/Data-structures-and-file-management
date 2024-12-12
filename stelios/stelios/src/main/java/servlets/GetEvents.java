package servlets;

import database.tables.EditEventsTable;
import mainClasses.Event;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet to get all events.
 */
public class GetEvents extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            EditEventsTable eet = new EditEventsTable();

            // Get all events
            ArrayList<Event> events = eet.getAllEvents();

            // Convert the list to JSON
            String json = new Gson().toJson(events);
            out.println(json);

            response.setStatus(200);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(GetEvents.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Set error status code
        }
    }

    // Other methods and class closing...
}

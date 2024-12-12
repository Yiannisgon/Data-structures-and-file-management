package servlets;

import database.tables.EditEventsTable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet to delete an event from the database.
 */
public class DeleteEvent extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String eventId = request.getParameter("event_id");

        try {
            EditEventsTable editEventsTable = new EditEventsTable();

            if (eventId != null && !eventId.isEmpty()) {
                editEventsTable.deleteEventById(eventId);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Event with ID " + eventId + " successfully deleted.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid event ID.");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DeleteEvent.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("An error occurred while deleting the event.");
        }
    }
}

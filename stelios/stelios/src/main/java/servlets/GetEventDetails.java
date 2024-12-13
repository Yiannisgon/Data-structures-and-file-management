package servlets;

import database.tables.EditEventsTable;
import mainClasses.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import com.google.gson.Gson;

@WebServlet(name = "GetEventDetails", urlPatterns = {"/GetEventDetails"})
public class GetEventDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String eventIdParam = request.getParameter("event_id");

        try {
            System.out.println("Received event_id: " + eventIdParam);
            int eventId = Integer.parseInt(eventIdParam);
            EditEventsTable editEventsTable = new EditEventsTable();
            Event event = editEventsTable.getEventById(eventId);

            if (event != null) {
                System.out.println("Fetched Event: " + event);
                Gson gson = new Gson();
                String json = gson.toJson(event);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(json);
            } else {
                System.out.println("No event found for event_id: " + eventId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Event not found.");
            }
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error: " + e.getMessage());
        }

    }
}

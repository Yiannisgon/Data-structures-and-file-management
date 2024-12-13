package servlets;

import database.tables.EditEventsTable;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetAvailableAndReservedSeats", urlPatterns = {"/GetAvailableAndReservedSeats"})
public class GetAvailableAndReservedSeats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String eventIdParam = request.getParameter("event_id");

        if (eventIdParam == null || eventIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Invalid or missing event ID.\"}");
            return;
        }

        try {
            int eventId = Integer.parseInt(eventIdParam);
            EditEventsTable editEventsTable = new EditEventsTable();
            int[] seats = editEventsTable.getAvailableAndReservedSeats(eventId);

            if (seats == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"message\": \"No event found with the given ID.\"}");
            } else {
                String jsonResponse = String.format("{\"availableSeats\": %d, \"reservedSeats\": %d}", seats[0], seats[1]);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(jsonResponse);
            }
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Event ID must be a valid integer.\"}");
        } catch (Exception ex) {
            Logger.getLogger(GetAvailableAndReservedSeats.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while fetching seat details.\"}");
        }
    }
}

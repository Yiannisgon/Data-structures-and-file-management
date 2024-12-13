package servlets;

import database.tables.EditEventsTable;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetRevenuePerEvent", urlPatterns = {"/GetRevenuePerEvent"})
public class GetRevenuePerEvent extends HttpServlet {

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
            double revenue = editEventsTable.getRevenueForEvent(eventId);

            if (revenue >= 0) {
                String jsonResponse = String.format("{\"revenue\": %.2f}", revenue);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"message\": \"No revenue data found for the given event.\"}");
            }
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Event ID must be a valid integer.\"}");
        } catch (Exception ex) {
            Logger.getLogger(GetRevenuePerEvent.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while fetching revenue data.\"}");
        }
    }
}

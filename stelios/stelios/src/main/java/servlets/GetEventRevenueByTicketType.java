package servlets;

import com.google.gson.JsonObject;
import database.tables.EditReservationsTable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetEventRevenueByTicketType", urlPatterns = {"/GetEventRevenueByTicketType"})
public class GetEventRevenueByTicketType extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String eventName = request.getParameter("event_name");
        String ticketType = request.getParameter("ticket_type");

        if (eventName == null || ticketType == null || eventName.isEmpty() || ticketType.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Invalid or missing parameters.\"}");
            return;
        }

        try {
            EditReservationsTable editReservationsTable = new EditReservationsTable();
            float revenue = editReservationsTable.getRevenueByEventNameAndTicketType(eventName, ticketType);

            JsonObject result = new JsonObject();
            result.addProperty("revenue", revenue);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(result.toString());
        } catch (Exception ex) {
            Logger.getLogger(GetEventRevenueByTicketType.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while calculating revenue.\"}");
        }
    }
}

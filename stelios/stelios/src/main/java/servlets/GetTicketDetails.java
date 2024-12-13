package servlets;

import database.tables.EditTicketsTable;
import mainClasses.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import com.google.gson.Gson;

@WebServlet(name = "GetTicketDetails", urlPatterns = {"/GetTicketDetails"})
public class GetTicketDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String eventIdParam = request.getParameter("event_id");
        String ticketType = request.getParameter("type");

        try {
            int eventId = Integer.parseInt(eventIdParam);
            EditTicketsTable editTicketsTable = new EditTicketsTable();
            Ticket ticket = editTicketsTable.getTicketByEventAndType(eventId, ticketType);

            if (ticket != null) {
                // Serialize the ticket object to JSON
                Gson gson = new Gson();
                String json = gson.toJson(ticket);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(json);

                System.out.println("Received event_id: " + eventIdParam);
                System.out.println("Received ticketType: " + ticketType);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Ticket not found.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}

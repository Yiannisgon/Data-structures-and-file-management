package servlets;

import com.google.gson.JsonObject;
import database.tables.EditEventsTable;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetMostTicketsReservedEvent", urlPatterns = {"/GetMostTicketsReservedEvent"})
public class GetMostTicketsReservedEvent extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            EditEventsTable editEventsTable = new EditEventsTable();
            JsonObject result = editEventsTable.getMostTicketsReservedEvent();

            if (result != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(result.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"message\": \"No events found.\"}");
            }
        } catch (Exception ex) {
            Logger.getLogger(GetMostTicketsReservedEvent.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while fetching the data.\"}");
        }
    }
}

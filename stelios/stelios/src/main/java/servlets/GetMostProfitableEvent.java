package servlets;

import com.google.gson.JsonObject;
import database.tables.EditEventsTable;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetMostProfitableEvent", urlPatterns = {"/GetMostProfitableEvent"})
public class GetMostProfitableEvent extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");

        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Invalid or missing date range.\"}");
            return;
        }

        try {
            EditEventsTable editEventsTable = new EditEventsTable();
            JsonObject result = editEventsTable.getMostProfitableEventInTimeRange(startDate, endDate);

            if (result != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(result.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"message\": \"No events found for the given time range.\"}");
            }
        } catch (Exception ex) {
            Logger.getLogger(GetMostProfitableEvent.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while fetching the data.\"}");
        }
    }
}

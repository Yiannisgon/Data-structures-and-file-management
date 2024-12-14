package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.tables.EditReservationsTable;
import mainClasses.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetReservationsByTimePeriod", urlPatterns = {"/GetReservationsByTimePeriod"})
public class GetReservationsByTimePeriod extends HttpServlet {

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
            EditReservationsTable editReservationsTable = new EditReservationsTable();
            List<JsonObject> reservations = editReservationsTable.getReservationsWithDetailsByTimePeriod(startDate, endDate);

            if (!reservations.isEmpty()) {
                String jsonResponse = new Gson().toJson(reservations);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("[]");
            }
        } catch (Exception ex) {
            Logger.getLogger(GetReservationsByTimePeriod.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while fetching the data.\"}");
        }
    }
}

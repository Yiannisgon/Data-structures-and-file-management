package servlets;

import database.tables.EditReservationsTable;
import mainClasses.Reservation;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GetReservationsByEmail", urlPatterns = {"/GetReservationsByEmail"})
public class GetReservationsByEmail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Invalid email.\"}");
            return;
        }

        try {
            EditReservationsTable editReservationsTable = new EditReservationsTable();
            ArrayList<Reservation> reservations = editReservationsTable.getReservationsByEmail(email);

            if (reservations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"message\": \"No reservations found for this email.\"}");
            } else {
                // Convert reservations to JSON and send back as response
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(reservations);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(jsonResponse);
            }
        } catch (Exception ex) {
            Logger.getLogger(GetReservationsByEmail.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"An error occurred while fetching reservations.\"}");
        }
    }
}

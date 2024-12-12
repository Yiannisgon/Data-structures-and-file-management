package servlets;

import database.tables.EditReservationsTable;
import mainClasses.Reservation;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CreateReservation", urlPatterns = {"/CreateReservation"})
public class CreateReservation extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        StringBuilder sb = new StringBuilder();
        String line;

        // Read the JSON input
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            response.setStatus(400);
            response.getWriter().write("Error reading input: " + e.getMessage());
            return;
        }

        String requestData = sb.toString();
        Gson gson = new Gson();
        Reservation reservation;

        try {
            // Parse JSON to Reservation object
            reservation = gson.fromJson(requestData, Reservation.class);

            // Validate Reservation data
            if (reservation.getTicketCount() <= 0 || reservation.getPaymentAmount() <= 0 ||
                    reservation.getCustomerId() <= 0 || reservation.getEventId() <= 0) {
                response.setStatus(400);
                response.getWriter().write("Invalid reservation data.");
                return;
            }

            // Add reservation to database
            EditReservationsTable ert = new EditReservationsTable();
            ert.addReservation(reservation);

            response.setStatus(200);
            response.getWriter().write("Reservation created successfully.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CreateReservation.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);
            response.getWriter().write("Database error: " + ex.getMessage());
        }
    }
}

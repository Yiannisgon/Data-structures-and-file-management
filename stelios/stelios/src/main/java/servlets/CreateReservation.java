package servlets;

import database.tables.EditReservationsTable;
import mainClasses.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CreateReservation", urlPatterns = {"/CreateReservation"})
public class CreateReservation extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Opened CreateReservation");
        response.setContentType("application/json;charset=UTF-8");

        // Read JSON from request
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            response.setStatus(400); // Bad request
            response.getWriter().write("Error reading input: " + e.getMessage());
            System.err.println("Error reading input: " + e.getMessage());
            return;
        }

        String requestData = sb.toString();
        System.out.println("Received JSON: " + requestData);

        try (PrintWriter out = response.getWriter()) {
            // Initialize Gson with a custom deserializer for Timestamp
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
                        try {
                            return Timestamp.valueOf(json.getAsString());
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid timestamp format. Expected 'YYYY-MM-DD HH:mm:ss'.");
                        }
                    })
                    .create();

            // Parse JSON to Reservation object
            Reservation newReservation;
            try {
                newReservation = gson.fromJson(requestData, Reservation.class);
                System.out.println("JSON parsed successfully.");
            } catch (Exception e) {
                System.err.println("Error during JSON parsing: " + e.getMessage());
                response.setStatus(400);
                response.getWriter().write("Invalid JSON format: " + e.getMessage());
                return;
            }

            // Debug log parsed Reservation
            System.out.println("Parsed Reservation Details:");
            System.out.println("Customer ID: " + newReservation.getCustomerId());
            System.out.println("Event ID: " + newReservation.getEventId());
            System.out.println("Ticket Count: " + newReservation.getTicketCount());
            System.out.println("Payment Amount: " + newReservation.getPaymentAmount());
            System.out.println("Reservation Date: " + newReservation.getReservationDate());

            // Validate fields
            if (newReservation.getCustomerId() <= 0 ||
                    newReservation.getEventId() <= 0 ||
                    newReservation.getTicketCount() <= 0 ||
                    newReservation.getPaymentAmount() <= 0 ||
                    newReservation.getReservationDate() == null) {
                System.err.println("Validation failed: Missing or invalid required fields.");
                response.setStatus(400); // Bad request
                response.getWriter().write("Missing or invalid required fields: customerId, eventId, ticketCount, paymentAmount, or reservationDate.");
                return;
            }

            // Add Reservation to database
            EditReservationsTable ert = new EditReservationsTable();
            try {
                ert.addReservation(newReservation);
                System.out.println("Reservation successfully added to database.");
            } catch (Exception ex) {
                System.err.println("Database error while adding reservation: " + ex.getMessage());
                response.setStatus(500);
                response.getWriter().write("Database error: " + ex.getMessage());
                return;
            }

            // Success response
            response.setStatus(200);
            out.println(gson.toJson(newReservation)); // Optionally return added reservation as JSON
        }
    }
}

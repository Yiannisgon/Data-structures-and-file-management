package servlets;

import database.tables.EditCustomersTable;
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
            Reservation newReservation = gson.fromJson(requestData, Reservation.class);

            // Validate fields
            if (newReservation.getCustomerId() <= 0 ||
                    newReservation.getEventId() <= 0 ||
                    newReservation.getTicketCount() <= 0 ||
                    newReservation.getPaymentAmount() <= 0 ||
                    newReservation.getReservationDate() == null) {
                response.setStatus(400); // Bad request
                out.write("Missing or invalid required fields.");
                return;
            }

            EditCustomersTable ect = new EditCustomersTable();
            EditReservationsTable ert = new EditReservationsTable();

            try {
                // Fetch the customer's current balance
                float customerBalance = ect.getCustomerBalance(newReservation.getCustomerId());

                // Validate sufficient balance
                if (customerBalance < newReservation.getPaymentAmount()) {
                    response.setStatus(400); // Bad request
                    out.write("Insufficient balance for the reservation.");
                    return;
                }

                // Proceed with reservation creation and balance deduction
                ert.addReservation(newReservation);

                // Success response
                response.setStatus(200);
                out.println(gson.toJson(newReservation));
                System.out.println("Reservation successfully created.");
            } catch (Exception ex) {
                System.err.println("Error during reservation creation: " + ex.getMessage());
                response.setStatus(500); // Internal Server Error
                out.write("Error during reservation creation: " + ex.getMessage());
            }
        }
    }
}

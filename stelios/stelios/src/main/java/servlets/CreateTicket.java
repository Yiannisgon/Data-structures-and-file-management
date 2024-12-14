package servlets;

import database.tables.EditTicketsTable;
import mainClasses.Ticket;
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
import java.sql.Date;

@WebServlet(name = "CreateTicket", urlPatterns = {"/CreateTicket"})
public class CreateTicket extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Opened CreateTicket");
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
            // Initialize Gson with custom deserializers for Date
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                        try {
                            return Date.valueOf(json.getAsString());
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid date format. Expected 'YYYY-MM-DD'.");
                        }
                    })
                    .create();

            // Parse JSON to Ticket object
            Ticket newTicket;
            try {
                newTicket = gson.fromJson(requestData, Ticket.class);
                System.out.println("JSON parsed successfully.");
            } catch (Exception e) {
                System.err.println("Error during JSON parsing: " + e.getMessage());
                response.setStatus(400);
                response.getWriter().write("Invalid JSON format: " + e.getMessage());
                return;
            }

            // Debug log parsed Ticket
            System.out.println("Parsed Ticket Details:");
            System.out.println("Availability: " + newTicket.getAvailability());
            System.out.println("Price: " + newTicket.getPrice());
            System.out.println("Type: " + newTicket.getType());
            System.out.println("Event ID: " + newTicket.getEvent_id());

            // Validate fields
            if (newTicket.getAvailability() <= 0 || newTicket.getPrice() <= 0 || newTicket.getType() == null || newTicket.getType().isEmpty() || newTicket.getEvent_id() <= 0) {
                System.err.println("Validation failed: Missing or invalid required fields.");
                response.setStatus(400); // Bad request
                response.getWriter().write("Missing or invalid required fields: availability, price, type, or event_id.");
                return;
            }

            // Add Ticket to database
            EditTicketsTable ett = new EditTicketsTable();
            try {
                ett.addTicket(newTicket);
                System.out.println("Ticket successfully added to database.");
            } catch (Exception ex) {
                System.err.println("Database error while adding ticket: " + ex.getMessage());
                response.setStatus(500);
                response.getWriter().write("Database error: " + ex.getMessage());
                return;
            }

            // Success response
            response.setStatus(200);
            out.println(gson.toJson(newTicket)); // Optionally return added ticket as JSON
        }
    }
}

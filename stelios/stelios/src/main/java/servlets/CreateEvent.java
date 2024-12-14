package servlets;

import database.tables.EditEventsTable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainClasses.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

@WebServlet(name = "CreateEvent", urlPatterns = {"/CreateEvent"})
public class CreateEvent extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Opened CreateEvent");
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
            // Initialize Gson with custom deserializers for Date and Time
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                        try {
                            return Date.valueOf(json.getAsString());
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid date format. Expected 'YYYY-MM-DD'.");
                        }
                    })
                    .registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (json, typeOfT, context) -> {
                        try {
                            return Time.valueOf(json.getAsString());
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid time format. Expected 'HH:mm:ss'.");
                        }
                    })
                    .create();

            // Parse JSON to Event object
            Event newEvent;
            try {
                newEvent = gson.fromJson(requestData, Event.class);
                System.out.println("JSON parsed successfully.");
            } catch (Exception e) {
                System.err.println("Error during JSON parsing: " + e.getMessage());
                response.setStatus(400);
                response.getWriter().write("Invalid JSON format: " + e.getMessage());
                return;
            }

            // Debug log parsed Event
            System.out.println("Parsed Event Details:");
            System.out.println("Name: " + newEvent.getName());
            System.out.println("Date: " + newEvent.getDate());
            System.out.println("Time: " + newEvent.getTime());
            System.out.println("Capacity: " + newEvent.getCapacity());
            System.out.println("Type: " + newEvent.getType());

            // Validate fields
            if (newEvent.getName() == null || newEvent.getName().isEmpty() ||
                    newEvent.getDate() == null ||
                    newEvent.getTime() == null ||
                    newEvent.getCapacity() <= 0 ||
                    newEvent.getType() == null || newEvent.getType().isEmpty()) {
                System.err.println("Validation failed: Missing or invalid required fields.");
                response.setStatus(400); // Bad request
                response.getWriter().write("Missing or invalid required fields: name, date, time, capacity, or type.");
                return;
            }

            // Add Event to database
            EditEventsTable eet = new EditEventsTable();
            try {
                // Add the event and get the generated event_id
                int eventId = eet.addEvent(newEvent);
                newEvent.setEventId(eventId); // Set the eventId to the newly created event

                System.out.println("Event successfully added to database.");
            } catch (Exception ex) {
                System.err.println("Database error while adding event: " + ex.getMessage());
                response.setStatus(500);
                response.getWriter().write("Database error: " + ex.getMessage());
                return;
            }

            // Success response, send the event details along with event_id
            response.setStatus(200);
            out.println(gson.toJson(newEvent)); // Return the event with the event_id
        }
    }
}

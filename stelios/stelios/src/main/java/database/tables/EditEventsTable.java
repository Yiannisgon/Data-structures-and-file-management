package database.tables;

import com.google.gson.*;
import mainClasses.Event;
import database.DB_Connection;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditEventsTable {

    public void addEventFromJSON(String json) throws ClassNotFoundException {
        System.out.println("Received JSON for Event: bb " + json);
        Event event = jsonToEvent(json);

        // Log parsed Event details
        System.out.println("Parsed Event Details:");
        System.out.println("Name: " + event.getName());
        System.out.println("Capacity: " + event.getCapacity());
        System.out.println("Date: " + event.getDate());
        System.out.println("Time: " + event.getTime());
        System.out.println("Type: " + event.getType());

        // Add event to database
        addEvent(event);
    }


    public Event jsonToEvent(String json) {
        Gson gson = new Gson();
        Event event = null;

        try {
            event = gson.fromJson(json, Event.class);
            System.out.println("JSON successfully parsed to Event object.");
        } catch (Exception e) {
            System.err.println("Error parsing JSON to Event: " + e.getMessage());
            e.printStackTrace();
        }

        return event;
    }


    public String eventToJSON(Event event) {
        Gson gson = new Gson();
        return gson.toJson(event, Event.class);
    }

    public void createEventsTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE events ("
                + "event_id INTEGER not NULL AUTO_INCREMENT, "
                + "name VARCHAR(100) NOT NULL, "
                + "capacity INTEGER not NULL, "
                + "date DATE not NULL, "
                + "time TIME not NULL, "
                + "type VARCHAR(50) not NULL, "
                + "PRIMARY KEY (event_id))";
        stmt.execute(query);
        stmt.close();
    }

    public int addEvent(Event event) throws ClassNotFoundException {
        int eventId = 0;
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();
            System.out.println("EVENT BEING ADDED");

            // Fix: Enclose `name` and `type` in single quotes
            String insertQuery = "INSERT INTO events (name, capacity, date, time, type) VALUES ("
                    + "'" + event.getName() + "'," // Enclosed in single quotes
                    + event.getCapacity() + "," // Capacity is an integer, no quotes needed
                    + "'" + event.getDate() + "'," // Date in single quotes
                    + "'" + event.getTime() + "'," // Time in single quotes
                    + "'" + event.getType() + "'" // Type in single quotes
                    + ")";
            System.out.println("Executing Query: " + insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The event was successfully added to the database.");

            // Retrieve generated event_id
            String getGeneratedIdQuery = "SELECT LAST_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(getGeneratedIdQuery);
            if (rs.next()) {
                eventId = rs.getInt(1); // Retrieve the generated event_id
                System.out.println("Generated event_id: " + eventId);
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditEventsTable.class.getName()).log(Level.SEVERE, "Database Error: " + ex.getMessage(), ex);
        }
        return eventId;
    }


    public Event databaseToEvent(int eventId) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM events WHERE event_id=" + eventId);
            if (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                return gson.fromJson(json, Event.class);
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        } finally {
            stmt.close();
        }
        return null;
    }

    public ArrayList<Event> getAllEvents() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Event> events = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM events");

            // Configure Gson with custom deserializer for time fields
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Time.class, new JsonDeserializer<Time>() {
                        @Override
                        public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            try {
                                return Time.valueOf(json.getAsString()); // Converts "HH:mm:ss" to java.sql.Time
                            } catch (IllegalArgumentException e) {
                                throw new JsonParseException("Invalid time format: " + json.getAsString(), e);
                            }
                        }
                    })
                    .setDateFormat("yyyy-MM-dd") // For date fields
                    .create();

            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                events.add(gson.fromJson(json, Event.class));
            }
        } catch (Exception e) {
            System.err.println("Got an exception while fetching events: ");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return events;
    }
    public void deleteEvent(int eventId) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();
            String deleteQuery = "DELETE FROM events WHERE event_id=" + eventId;
            stmt.executeUpdate(deleteQuery);
            System.out.println("# The event was successfully deleted from the database.");
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditEventsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void deleteEventById(String eventId) throws ClassNotFoundException {
        String deleteQuery = "DELETE FROM events WHERE event_id = ?";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {

            // Step 1: Retrieve all reservations for the given event ID
            String getReservationsQuery = "SELECT customer_id, payment_amount FROM reservations WHERE event_id = ?";
            try (PreparedStatement reservationStmt = con.prepareStatement(getReservationsQuery)) {
                reservationStmt.setString(1, eventId);
                try (ResultSet rs = reservationStmt.executeQuery()) {

                    System.out.println("Checking reservations for event ID: " + eventId);
                    while (rs.next()) {
                        int customerId = rs.getInt("customer_id");
                        float paymentAmount = rs.getFloat("payment_amount");

                        // Step 2: Refund the amount to the customer's balance
                        String refundQuery = "UPDATE customers SET balance = balance + ? WHERE customer_id = ?";
                        try (PreparedStatement refundStmt = con.prepareStatement(refundQuery)) {
                            refundStmt.setFloat(1, paymentAmount);
                            refundStmt.setInt(2, customerId);
                            int affectedRows = refundStmt.executeUpdate();

                            if (affectedRows > 0) {
                                System.out.println("Refunded " + paymentAmount + " to customer ID: " + customerId);
                            } else {
                                System.err.println("Failed to refund to customer ID: " + customerId);
                            }
                        }
                    }
                }
            }

            // Step 3: Delete the reservations associated with the event
            String deleteReservationsQuery = "DELETE FROM reservations WHERE event_id = ?";
            try (PreparedStatement deleteReservationsStmt = con.prepareStatement(deleteReservationsQuery)) {
                deleteReservationsStmt.setString(1, eventId);
                int deletedReservations = deleteReservationsStmt.executeUpdate();
                System.out.println("Deleted " + deletedReservations + " reservations associated with event ID: " + eventId);
            }

            // Step 4: Delete the event
            pstmt.setString(1, eventId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("# The event was successfully deleted from the database.");
            } else {
                System.out.println("# No event found with the provided ID.");
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditEventsTable.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Error deleting the event from the database: " + ex.getMessage());
        }
    }

    public Event getEventById(int eventId) throws SQLException, ClassNotFoundException {
        Event event = null;
        String query = "SELECT name, capacity, date, time, type, event_id FROM events WHERE event_id = ?";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    event = new Event();
                    event.setName(rs.getString("name"));
                    event.setCapacity(rs.getInt("capacity"));
                    event.setDate(rs.getDate("date"));
                    event.setTime(rs.getTime("time"));
                    event.setType(rs.getString("type"));
                    event.setEventId(rs.getInt("event_id"));

                    // Log combined date and time
                    String combinedDateTime = rs.getDate("date") + " " + rs.getTime("time");
                    System.out.println("Combined DateTime: " + combinedDateTime);
                }
            }
        }
        return event;
    }

    public int[] getAvailableAndReservedSeats(int eventId) throws SQLException, ClassNotFoundException {
        String query = "SELECT e.capacity - COALESCE(SUM(r.ticket_count), 0) AS available_seats, " +
                "COALESCE(SUM(r.ticket_count), 0) AS reserved_seats " +
                "FROM events e " +
                "LEFT JOIN reservations r ON e.event_id = r.event_id " +
                "WHERE e.event_id = ? " +
                "GROUP BY e.capacity";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int availableSeats = rs.getInt("available_seats");
                    int reservedSeats = rs.getInt("reserved_seats");
                    return new int[]{availableSeats, reservedSeats};
                }
            }
        }
        return null; // If no event or reservations found
    }

    public float getRevenueForEvent(int eventId) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(r.payment_amount) AS total_revenue " +
                "FROM reservations r WHERE r.event_id = ?";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("total_revenue");
                }
            }
        }

        return -1; // Return -1 if no revenue data found
    }

    public JsonObject getMostTicketsReservedEvent() throws SQLException, ClassNotFoundException {
        String query = "SELECT e.name AS event_name, SUM(r.ticket_count) AS ticket_count " +
                "FROM events e " +
                "JOIN reservations r ON e.event_id = r.event_id " +
                "GROUP BY e.event_id, e.name " +
                "ORDER BY ticket_count DESC " +
                "LIMIT 1";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    JsonObject result = new JsonObject();
                    result.addProperty("eventName", rs.getString("event_name"));
                    result.addProperty("ticketCount", rs.getInt("ticket_count"));
                    return result;
                }
            }
        }
        return null; // If no data found
    }


    public JsonObject getMostProfitableEventInTimeRange(String startDate, String endDate) throws SQLException, ClassNotFoundException {
        String query = "SELECT e.name AS event_name, SUM(r.payment_amount) AS total_revenue " +
                "FROM events e " +
                "JOIN reservations r ON e.event_id = r.event_id " +
                "WHERE r.reservation_date BETWEEN ? AND ? " +
                "GROUP BY e.event_id, e.name " +
                "ORDER BY total_revenue DESC " +
                "LIMIT 1";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    JsonObject result = new JsonObject();
                    result.addProperty("eventName", rs.getString("event_name"));
                    result.addProperty("revenue", rs.getFloat("total_revenue"));
                    return result;
                }
            }
        }

        return null; // Return null if no data found
    }

}
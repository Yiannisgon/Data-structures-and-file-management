package database.tables;

import mainClasses.Customer;
import mainClasses.Ticket;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditTicketsTable {

    /**
     * Creates the tickets table in the database.
     */


    public void addTicketFromJSON(String json) throws ClassNotFoundException {
        System.out.println("Parsing Ticket JSON...");
        Ticket ticket = jsonToTicket(json);
        System.out.println("Parsed Ticket: " + ticket);
        System.out.println("Adding Ticket...");
        addTicket(ticket);
    }

    public Ticket jsonToTicket(String json) {
        Gson gson = new Gson();
        Ticket ticket = null;

        try {
            ticket = gson.fromJson(json, Ticket.class);
            System.out.println("JSON successfully parsed to Ticket object.");
        } catch (Exception e) {
            System.err.println("Error parsing JSON to Ticket: " + e.getMessage());
            e.printStackTrace();
        }

        return ticket;
    }

    public void createTicketsTable() throws SQLException, ClassNotFoundException {
        String query = "CREATE TABLE IF NOT EXISTS tickets ("
                + "ticket_id INTEGER not NULL AUTO_INCREMENT, "
                + "availability INTEGER not NULL, "
                + "price FLOAT not NULL, "
                + "type VARCHAR(50) not NULL, "
                + "event_id INTEGER not NULL, "
                + "PRIMARY KEY (ticket_id), "
                + "FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE ON UPDATE CASCADE)";

        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            System.out.println("Creating Tickets Table...");
            stmt.execute(query);
            System.out.println("Tickets Table successfully created (or already exists).");

        } catch (SQLException ex) {
            System.err.println("Error creating Tickets Table: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Adds a new ticket to the database.
     */
    public void addTicket(Ticket ticket) throws ClassNotFoundException {
        // Validate Ticket object
        if (ticket == null || ticket.getAvailability() <= 0 || ticket.getPrice() <= 0 ||
                ticket.getType() == null || ticket.getType().isEmpty() || ticket.getEvent_id() <= 0) {
            throw new IllegalArgumentException("Invalid ticket data: " + ticket);
        }

        String insertQuery = "INSERT INTO tickets (availability, price, type, event_id) VALUES (?, ?, ?, ?)";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(insertQuery)) {

            // Set parameters for the prepared statement
            pstmt.setInt(1, ticket.getAvailability());
            pstmt.setFloat(2, ticket.getPrice());
            pstmt.setString(3, ticket.getType());
            pstmt.setInt(4, ticket.getEvent_id());

            // Debug log for the query execution
            System.out.println("Executing Query: " + pstmt);

            // Execute the update
            pstmt.executeUpdate();
            System.out.println("# The ticket was successfully added to the database.");

        } catch (SQLException ex) {
            System.err.println("Error adding ticket to the database: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error adding ticket: " + ex.getMessage());
        }
    }

    /**
     * Deletes a ticket from the database by its ID.
     */

    // Method to get total revenue for both ticket types
    public Map<String, Float> getTotalRevenueForAllTypes() throws SQLException, ClassNotFoundException {
        String query = "SELECT T.type AS TicketType, SUM(R.ticket_count * T.price) AS TotalRevenue "
                + "FROM tickets T "
                + "JOIN reservations R ON T.event_id = R.event_id AND T.type = R.ticket_type "
                + "GROUP BY T.type";

        Map<String, Float> revenueMap = new HashMap<>();

        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Debugging Revenue Calculation:");

            // Step 1: Debug query execution
            System.out.println("Executing SQL Query: " + query);

            // Step 2: Loop through results and debug each row
            while (rs.next()) {
                String ticketType = rs.getString("TicketType");
                float totalRevenue = rs.getFloat("TotalRevenue");

                // Debug print for each ticket type's revenue
                System.out.println("Ticket Type: " + ticketType + ", Total Revenue: " + totalRevenue);

                // Add to the revenue map
                revenueMap.put(ticketType, totalRevenue);
            }

            // Step 3: Debug total revenue calculation
            float totalCalculatedRevenue = revenueMap.values().stream().reduce(0.0f, Float::sum);
            System.out.println("Calculated Total Revenue (All Types): " + totalCalculatedRevenue);

            // Step 4: Debug the final map content
            System.out.println("Revenue Map: " + revenueMap);

        } catch (SQLException ex) {
            Logger.getLogger(EditTicketsTable.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Error fetching total revenue: " + ex.getMessage());
        }

        return revenueMap;
    }



    public Ticket getTicketByEventAndType(int eventId, String ticketType) throws SQLException, ClassNotFoundException {
        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement("SELECT * FROM tickets WHERE event_id = ? AND type = ?")) {
            pstmt.setInt(1, eventId);
            pstmt.setString(2, ticketType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String json = DB_Connection.getResultsToJSON(rs);
                    Gson gson = new Gson();
                    return gson.fromJson(json, Ticket.class);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching ticket: " + ex.getMessage());
            throw ex;
        }
        return null;
    }


}

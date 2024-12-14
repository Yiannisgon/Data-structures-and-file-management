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

    public String ticketToJSON(Ticket ticket) {
        Gson gson = new Gson();
        return gson.toJson(ticket, Ticket.class);
    }
    public void createTicketsTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE tickets ("
                + "ticket_id INTEGER not NULL AUTO_INCREMENT, "
                + "availability INTEGER not NULL, "
                + "price FLOAT not NULL, "
                + "type VARCHAR(50) not NULL, "
                + "event_id INTEGER not NULL, "
                + "PRIMARY KEY (ticket_id), "
                + "FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE ON UPDATE CASCADE)";

        stmt.execute(query);
        stmt.close();
    }

    /**
     * Adds a new ticket to the database.
     */
    public void addTicket(Ticket ticket) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO tickets (availability, price, type, event_id) VALUES ("
                    + ticket.getAvailability() + ", "
                    + ticket.getPrice() + ", "
                    + "'" + ticket.getType() + "', "
                    + ticket.getEvent_id() // Assuming ticket has a getEventId() method
                    + ")";
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The ticket was successfully added to the database.");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditTicketsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves a ticket from the database by its ID.
     */
    public Ticket databaseToTicket(int ticketId) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tickets WHERE ticket_id=" + ticketId);
            if (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                return gson.fromJson(json, Ticket.class);
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        } finally {
            stmt.close();
        }
        return null;
    }

    /**
     * Retrieves all tickets from the database.
     */
    public ArrayList<Ticket> getAllTickets() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Ticket> tickets = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tickets");
            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                tickets.add(gson.fromJson(json, Ticket.class));
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        } finally {
            stmt.close();
        }
        return tickets;
    }

    /**
     * Updates the availability of a ticket by its ID.
     */
    public void updateTicketAvailability(int ticketId, int availability) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String updateQuery = "UPDATE tickets SET availability=" + availability + " WHERE ticket_id=" + ticketId;
        stmt.executeUpdate(updateQuery);
        stmt.close();
    }

    /**
     * Deletes a ticket from the database by its ID.
     */
    public void deleteTicket(int ticketId) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();

            String deleteQuery = "DELETE FROM tickets WHERE ticket_id=" + ticketId;
            stmt.executeUpdate(deleteQuery);
            System.out.println("# The ticket was successfully deleted from the database.");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditTicketsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

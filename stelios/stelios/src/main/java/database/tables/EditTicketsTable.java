package database.tables;

import mainClasses.Customer;
import mainClasses.Ticket;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.*;
import java.util.ArrayList;
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
                + "PRIMARY KEY (ticket_id))";
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

            String insertQuery = "INSERT INTO tickets (availability, price, type) VALUES ("
                    + ticket.getAvailability() + ", "
                    + ticket.getPrice() + ", "
                    + "'" + ticket.getType() + "'"
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
}

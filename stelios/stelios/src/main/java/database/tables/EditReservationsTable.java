package database.tables;

import mainClasses.Reservation;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditReservationsTable {

    public void addReservationFromJSON(String json) throws ClassNotFoundException {
        System.out.println("hoy");
        Reservation reservation = jsonToReservation(json);
        System.out.println("Parsed Reservation: " + reservation);
        System.out.println("hey");
        addReservation(reservation);
    }


    public Reservation jsonToReservation(String json) {
        Gson gson = new Gson();
        Reservation reservation = null;

        try {
            reservation = gson.fromJson(json, Reservation.class);
            System.out.println("JSON successfully parsed to Reservation object.");
        } catch (Exception e) {
            System.err.println("Error parsing JSON to Reservation: " + e.getMessage());
            e.printStackTrace();
        }

        return reservation;
    }

    public String reservationToJSON(Reservation reservation) {
        Gson gson = new Gson();
        return gson.toJson(reservation, Reservation.class);
    }

    public void createReservationsTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE reservations ("
                + "reservation_id INTEGER NOT NULL AUTO_INCREMENT, "
                + "customer_id INTEGER NOT NULL, "
                + "event_id INTEGER NOT NULL, "
                + "ticket_count INTEGER NOT NULL, "
                + "payment_amount FLOAT NOT NULL, "
                + "reservation_date TIMESTAMP NOT NULL, "
                + "PRIMARY KEY (reservation_id), "
                + "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) "
                + "ON DELETE CASCADE ON UPDATE CASCADE, "
                + "FOREIGN KEY (event_id) REFERENCES events(event_id) "
                + "ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";

        stmt.execute(query);
        stmt.close();
    }

    public void addReservation(Reservation reservation) throws ClassNotFoundException {
        Connection con = null;
        try {
            con = DB_Connection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Insert reservation
            String insertQuery = "INSERT INTO reservations (customer_id, event_id, ticket_count, payment_amount, reservation_date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                pstmt.setInt(1, reservation.getCustomerId());
                pstmt.setInt(2, reservation.getEventId());
                pstmt.setInt(3, reservation.getTicketCount());
                pstmt.setFloat(4, reservation.getPaymentAmount());
                pstmt.setTimestamp(5, reservation.getReservationDate());
                pstmt.executeUpdate();
                System.out.println("# The reservation was successfully added to the database.");
            }

            // Deduct payment amount from customer's balance
            String updateBalanceQuery = "UPDATE customers SET balance = balance - ? WHERE customer_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(updateBalanceQuery)) {
                pstmt.setFloat(1, reservation.getPaymentAmount());
                pstmt.setInt(2, reservation.getCustomerId());
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to update balance: Customer ID not found.");
                }
            }

            con.commit(); // Commit transaction
        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback(); // Rollback transaction on failure
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Error adding reservation or updating balance: " + ex.getMessage(), ex);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException closeEx) {
                    System.err.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public ArrayList<Reservation> getAllReservations() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Reservation> reservations = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM reservations");
            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                reservations.add(gson.fromJson(json, Reservation.class));
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        } finally {
            stmt.close();
        }
        return reservations;
    }
}

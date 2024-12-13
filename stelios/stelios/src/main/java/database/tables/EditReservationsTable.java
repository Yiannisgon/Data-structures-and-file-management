package database.tables;

import mainClasses.Reservation;
import com.google.gson.Gson;
import database.DB_Connection;

import java.sql.*;
import java.util.ArrayList;

public class EditReservationsTable {

    public void addReservationFromJSON(String json) throws ClassNotFoundException {
        System.out.println("Starting to add reservation from JSON...");
        System.out.println("Received JSON: " + json);
        Reservation reservation = jsonToReservation(json);
        if (reservation != null) {
            System.out.println("Parsed Reservation: " + reservationToJSON(reservation));
            addReservation(reservation);
        } else {
            System.err.println("Failed to parse reservation JSON.");
        }
    }

    public Reservation jsonToReservation(String json) {
        Gson gson = new Gson();
        try {
            Reservation reservation = gson.fromJson(json, Reservation.class);
            System.out.println("JSON successfully parsed to Reservation object: " + reservationToJSON(reservation));
            return reservation;
        } catch (Exception e) {
            System.err.println("Error parsing JSON to Reservation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String reservationToJSON(Reservation reservation) {
        Gson gson = new Gson();
        return gson.toJson(reservation, Reservation.class);
    }

    public void createReservationsTable() throws SQLException, ClassNotFoundException {
        try (Connection con = DB_Connection.getConnection(); Statement stmt = con.createStatement()) {
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
            System.out.println("Reservations table created successfully.");
        }
    }

    public void addReservation(Reservation reservation) throws ClassNotFoundException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation object cannot be null.");
        }

        System.out.println("Adding reservation: " + reservationToJSON(reservation));
        validateReservation(reservation);

        try (Connection con = DB_Connection.getConnection()) {
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
                System.out.println("Reservation successfully added for Customer ID: " + reservation.getCustomerId());
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
                System.out.println("Balance successfully updated for Customer ID: " + reservation.getCustomerId());
            }

            con.commit(); // Commit transaction
            System.out.println("Transaction committed successfully for reservation: " + reservationToJSON(reservation));
        } catch (SQLException ex) {
            System.err.println("Error adding reservation or updating balance: " + ex.getMessage());
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    public ArrayList<Reservation> getAllReservations() throws SQLException, ClassNotFoundException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations";

        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Reservation reservation = jsonToReservation(json);
                if (reservation != null) {
                    reservations.add(reservation);
                    System.out.println("Retrieved Reservation: " + reservationToJSON(reservation));
                }
            }
        }
        return reservations;
    }

    private void validateReservation(Reservation reservation) {
        System.out.println("Validating reservation: " + reservationToJSON(reservation));

        if (reservation.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid Customer ID: " + reservation.getCustomerId());
        }
        if (reservation.getEventId() <= 0) {
            throw new IllegalArgumentException("Invalid Event ID: " + reservation.getEventId());
        }
        if (reservation.getTicketCount() <= 0) {
            throw new IllegalArgumentException("Ticket count must be greater than 0.");
        }
        if (reservation.getPaymentAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0.");
        }
        if (reservation.getReservationDate() == null) {
            throw new IllegalArgumentException("Reservation date cannot be null.");
        }
    }
}

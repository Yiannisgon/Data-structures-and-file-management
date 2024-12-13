package database.tables;

import mainClasses.Reservation;
import com.google.gson.Gson;
import database.DB_Connection;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditReservationsTable {

    // Global variable to track the total refund tax withheld
    private static float totalRefundTax = 0.0f;

    public static float getTotalRefundTax() {
        return totalRefundTax;
    }

    public void addReservationFromJSON(String json) throws ClassNotFoundException {
        System.out.println("Starting to add reservation from JSON...");
        System.out.println("Received JSON: " + json);

        // Parse the JSON into a Reservation object
        Reservation reservation = jsonToReservation(json);

        // Extract ticketType from JSON
        String ticketType = extractTicketTypeFromJSON(json);

        if (reservation != null && ticketType != null) {
            System.out.println("Parsed Reservation: " + reservationToJSON(reservation));
            addReservation(reservation, ticketType);
        } else {
            System.err.println("Failed to parse reservation JSON or ticket type is missing.");
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

    // Method to extract ticketType from the JSON string
    private String extractTicketTypeFromJSON(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        if (jsonObject.has("ticketType")) {
            return jsonObject.get("ticketType").getAsString();
        }
        return null;  // Return null if ticketType is not found
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
                    + "ticket_type VARCHAR(50), " // Add ticket_type column to the table
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

    // Update to include ticketType when adding a reservation
    public void addReservation(Reservation reservation, String ticketType) throws ClassNotFoundException {
        if (reservation == null || ticketType == null) {
            throw new IllegalArgumentException("Reservation or Ticket Type cannot be null.");
        }

        System.out.println("Adding reservation for ticket type: " + ticketType);
        validateReservation(reservation);

        try (Connection con = DB_Connection.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            // Step 1: Check ticket availability
            String availabilityQuery = "SELECT availability FROM tickets WHERE event_id = ? AND type = ?";
            int availableTickets;

            try (PreparedStatement pstmt = con.prepareStatement(availabilityQuery)) {
                pstmt.setInt(1, reservation.getEventId());
                pstmt.setString(2, ticketType);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        availableTickets = rs.getInt("availability");
                    } else {
                        throw new IllegalArgumentException("No tickets found for the selected event and ticket type.");
                    }
                }
            }

            if (availableTickets < reservation.getTicketCount()) {
                throw new IllegalArgumentException("Not enough tickets available for the selected type.");
            }

            // Step 2: Deduct tickets from availability
            String updateAvailabilityQuery = "UPDATE tickets SET availability = availability - ? WHERE event_id = ? AND type = ?";
            try (PreparedStatement pstmt = con.prepareStatement(updateAvailabilityQuery)) {
                pstmt.setInt(1, reservation.getTicketCount());
                pstmt.setInt(2, reservation.getEventId());
                pstmt.setString(3, ticketType);
                pstmt.executeUpdate();
                System.out.println("Ticket availability successfully updated.");
            }

            // Step 3: Insert the reservation into the database
            String insertReservationQuery = "INSERT INTO reservations (customer_id, event_id, ticket_count, payment_amount, reservation_date, ticket_type) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertReservationQuery)) {
                pstmt.setInt(1, reservation.getCustomerId());
                pstmt.setInt(2, reservation.getEventId());
                pstmt.setInt(3, reservation.getTicketCount());
                pstmt.setFloat(4, reservation.getPaymentAmount());
                pstmt.setTimestamp(5, reservation.getReservationDate());
                pstmt.setString(6, ticketType);
                pstmt.executeUpdate();
                System.out.println("Reservation successfully added.");
            }

            // Step 4: Deduct payment amount from customer's balance
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

            con.commit(); // Commit the transaction
            System.out.println("Transaction committed successfully for reservation.");
        } catch (SQLException ex) {
            System.err.println("Error during reservation creation: " + ex.getMessage());
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    public ArrayList<Reservation> getAllReservations() throws SQLException, ClassNotFoundException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations";

        // Create a Gson instance with the custom deserializer for Reservation
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Reservation.class, new JsonDeserializer<Reservation>() {
                    @Override
                    public Reservation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();

                        Reservation reservation = new Reservation();
                        reservation.setReservationId(jsonObject.get("reservation_id").getAsInt());
                        reservation.setCustomerId(jsonObject.get("customer_id").getAsInt());
                        reservation.setEventId(jsonObject.get("event_id").getAsInt());
                        reservation.setTicketCount(jsonObject.get("ticket_count").getAsInt());
                        reservation.setPaymentAmount(jsonObject.get("payment_amount").getAsFloat());

                        // Parse the reservation date
                        String dateStr = jsonObject.get("reservation_date").getAsString();
                        reservation.setReservationDate(Timestamp.valueOf(dateStr.replace("T", " ").replace("Z", "")));

                        // Parse ticket_type and set it
                        if (jsonObject.has("ticket_type")) {
                            reservation.setTicketType(jsonObject.get("ticket_type").getAsString());
                        }

                        return reservation;
                    }
                })
                .create();

        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Convert the ResultSet row to JSON
                String json = DB_Connection.getResultsToJSON(rs);
                System.out.println("Raw Reservation JSON: " + json);

                // Deserialize the JSON into a Reservation object
                try {
                    Reservation reservation = gson.fromJson(json, Reservation.class);
                    reservations.add(reservation);
                    System.out.println("Retrieved Reservation: " + gson.toJson(reservation));
                } catch (JsonParseException e) {
                    System.err.println("Failed to parse reservation JSON: " + e.getMessage());
                    e.printStackTrace();
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

    public void deleteReservationById(String reservationId) throws ClassNotFoundException {
        String deleteQuery = "DELETE FROM reservations WHERE reservation_id = ?";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {

            // Step 1: Retrieve the reservation details
            String getReservationQuery = "SELECT customer_id, payment_amount FROM reservations WHERE reservation_id = ?";
            try (PreparedStatement reservationStmt = con.prepareStatement(getReservationQuery)) {
                reservationStmt.setString(1, reservationId);
                try (ResultSet rs = reservationStmt.executeQuery()) {

                    if (rs.next()) {
                        int customerId = rs.getInt("customer_id");
                        float paymentAmount = rs.getFloat("payment_amount");

                        // Step 2: Calculate the refund tax (e.g., 10%)
                        float refundTaxRate = 0.10f; // 10% refund tax
                        float refundTax = paymentAmount * refundTaxRate;
                        float refundAmount = paymentAmount - refundTax;

                        // Update the global refund tax tracker
                        totalRefundTax += refundTax;

                        // Step 3: Refund the remaining amount to the customer's balance
                        String refundQuery = "UPDATE customers SET balance = balance + ? WHERE customer_id = ?";
                        try (PreparedStatement refundStmt = con.prepareStatement(refundQuery)) {
                            refundStmt.setFloat(1, refundAmount);
                            refundStmt.setInt(2, customerId);
                            int affectedRows = refundStmt.executeUpdate();

                            if (affectedRows > 0) {
                                System.out.println("Refunded " + refundAmount + " to customer ID: " + customerId);
                                System.out.println("Withheld refund tax: " + refundTax);
                                System.out.println("Total refund tax withheld: " + totalRefundTax);
                            } else {
                                System.err.println("Failed to refund to customer ID: " + customerId);
                            }
                        }
                    } else {
                        System.err.println("No reservation found with ID: " + reservationId);
                        return; // Exit if no reservation is found
                    }
                }
            }

            // Step 4: Delete the reservation
            pstmt.setString(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("# The reservation with ID " + reservationId + " was successfully deleted from the database.");
            } else {
                System.out.println("# No reservation found with the provided ID.");
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditReservationsTable.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Error deleting the reservation from the database: " + ex.getMessage());
        }
    }

}

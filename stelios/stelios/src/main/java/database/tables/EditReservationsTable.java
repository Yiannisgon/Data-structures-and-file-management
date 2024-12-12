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
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO reservations (customer_id, event_id, ticket_count, payment_amount, reservation_date) VALUES ("
                    + reservation.getCustomerId() + ","
                    + reservation.getEventId() + ","
                    + reservation.getTicketCount() + ","
                    + reservation.getPaymentAmount() + ","
                    + "'" + reservation.getReservationDate() + "'"
                    + ")";
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The reservation was successfully added to the database.");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditReservationsTable.class.getName()).log(Level.SEVERE, null, ex);
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

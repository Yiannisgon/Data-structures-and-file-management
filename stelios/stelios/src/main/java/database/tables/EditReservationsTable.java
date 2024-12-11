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
        Reservation reservation = jsonToReservation(json);
        addReservation(reservation);
    }

    public Reservation jsonToReservation(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Reservation.class);
    }

    public String reservationToJSON(Reservation reservation) {
        Gson gson = new Gson();
        return gson.toJson(reservation, Reservation.class);
    }

    public void createReservationsTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE reservations ("
                + "res_id INTEGER not NULL AUTO_INCREMENT, "
                + "customer_id INTEGER not NULL, "
                + "event_id INTEGER not NULL, "
                + "ticket_count INTEGER not NULL, "
                + "payment_amount INTEGER not NULL, "
                + "reservation_date TIMESTAMP not NULL, "
                + "PRIMARY KEY (res_id))";
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

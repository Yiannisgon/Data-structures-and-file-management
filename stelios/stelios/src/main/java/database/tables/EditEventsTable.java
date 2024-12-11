package database.tables;

import mainClasses.Event;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditEventsTable {

    public void addEventFromJSON(String json) throws ClassNotFoundException {
        Event event = jsonToEvent(json);
        addEvent(event);
    }

    public Event jsonToEvent(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Event.class);
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
                + "capacity INTEGER not NULL, "
                + "date DATE not NULL, "
                + "time TIME not NULL, "
                + "type VARCHAR(50) not NULL, "
                + "PRIMARY KEY (event_id))";
        stmt.execute(query);
        stmt.close();
    }

    public void addEvent(Event event) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO events (name, capacity, date, time, type) VALUES ("
                    + event.getName() + ","
                    + event.getCapacity() + ","
                    + "'" + event.getDate() + "',"
                    + "'" + event.getTime() + "',"
                    + "'" + event.getType() + "'"
                    + ")";
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The event was successfully added to the database.");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditEventsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                events.add(gson.fromJson(json, Event.class));
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
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
}

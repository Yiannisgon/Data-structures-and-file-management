/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.init;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import database.tables.*;

import static database.DB_Connection.getInitialConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/*
 *
 * @author micha
 */
public class InitDatabase {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        InitDatabase init = new InitDatabase();
        init.initDatabase();
        init.initTables();
        init.addToDatabaseExamples();


        //  init.dropDatabase();
        // init.deleteRecords();
    }

    public void dropDatabase() throws SQLException, ClassNotFoundException {
        Connection conn = getInitialConnection();
        Statement stmt = conn.createStatement();
        String sql = "DROP DATABASE  HY360_2024";
        stmt.executeUpdate(sql);
        System.out.println("Database dropped successfully...");
    }

    public void initDatabase() throws SQLException, ClassNotFoundException {
        Connection conn = getInitialConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE DATABASE HY360_2024");
        stmt.close();
        conn.close();
    }

    public void initTables() throws SQLException, ClassNotFoundException {

        // New Classes
        EditCustomersTable editCustomers = new EditCustomersTable();
        editCustomers.createCustomersTable();

        EditEventsTable editEvents = new EditEventsTable();
        editEvents.createEventsTable();

        EditReservationsTable editReservations = new EditReservationsTable();
        editReservations.createReservationsTable();

        EditTicketsTable editTickets = new EditTicketsTable();
        editTickets.createTicketsTable();
    }

    public void addToDatabaseExamples() throws ClassNotFoundException, SQLException {
        //Users

        EditCustomersTable editCustomers = new EditCustomersTable();

// Add 15 customers
        editCustomers.addCustomerFromJSON(Resources.customer1);
        editCustomers.addCustomerFromJSON(Resources.customer2);
        editCustomers.addCustomerFromJSON(Resources.customer3);
        editCustomers.addCustomerFromJSON(Resources.customer4);
        editCustomers.addCustomerFromJSON(Resources.customer5);
        editCustomers.addCustomerFromJSON(Resources.customer6);
        editCustomers.addCustomerFromJSON(Resources.customer7);
        editCustomers.addCustomerFromJSON(Resources.customer8);
        editCustomers.addCustomerFromJSON(Resources.customer9);
        editCustomers.addCustomerFromJSON(Resources.customer10);
        editCustomers.addCustomerFromJSON(Resources.customer11);
        editCustomers.addCustomerFromJSON(Resources.customer12);
        editCustomers.addCustomerFromJSON(Resources.customer13);
        editCustomers.addCustomerFromJSON(Resources.customer14);
        editCustomers.addCustomerFromJSON(Resources.customer15);

        EditEventsTable editEvents = new EditEventsTable();

// Add 5 events
        editEvents.addEventFromJSON(Resources.event1);
        editEvents.addEventFromJSON(Resources.event2);
        editEvents.addEventFromJSON(Resources.event3);
        editEvents.addEventFromJSON(Resources.event4);
        editEvents.addEventFromJSON(Resources.event5);

        EditTicketsTable editTickets = new EditTicketsTable();

// Add 10 tickets (2 for each event)
        editTickets.addTicketFromJSON(Resources.ticket1);
        editTickets.addTicketFromJSON(Resources.ticket2);
        editTickets.addTicketFromJSON(Resources.ticket3);
        editTickets.addTicketFromJSON(Resources.ticket4);
        editTickets.addTicketFromJSON(Resources.ticket5);
        editTickets.addTicketFromJSON(Resources.ticket6);
        editTickets.addTicketFromJSON(Resources.ticket7);
        editTickets.addTicketFromJSON(Resources.ticket8);
        editTickets.addTicketFromJSON(Resources.ticket9);
        editTickets.addTicketFromJSON(Resources.ticket10);

        EditReservationsTable editReservations = new EditReservationsTable();

// Add 20 reservations
        System.out.println("Adding reservations...");
        editReservations.addReservationFromJSON(Resources.reservation1);
        editReservations.addReservationFromJSON(Resources.reservation2);
        editReservations.addReservationFromJSON(Resources.reservation3);
        editReservations.addReservationFromJSON(Resources.reservation4);
        editReservations.addReservationFromJSON(Resources.reservation5);
        editReservations.addReservationFromJSON(Resources.reservation6);
        editReservations.addReservationFromJSON(Resources.reservation7);
        editReservations.addReservationFromJSON(Resources.reservation8);
        editReservations.addReservationFromJSON(Resources.reservation9);
        editReservations.addReservationFromJSON(Resources.reservation10);
        editReservations.addReservationFromJSON(Resources.reservation11);
        editReservations.addReservationFromJSON(Resources.reservation12);
        editReservations.addReservationFromJSON(Resources.reservation13);
        editReservations.addReservationFromJSON(Resources.reservation14);
        editReservations.addReservationFromJSON(Resources.reservation15);
        editReservations.addReservationFromJSON(Resources.reservation16);
        editReservations.addReservationFromJSON(Resources.reservation17);
        editReservations.addReservationFromJSON(Resources.reservation18);
        editReservations.addReservationFromJSON(Resources.reservation19);
        editReservations.addReservationFromJSON(Resources.reservation20);

    }




}

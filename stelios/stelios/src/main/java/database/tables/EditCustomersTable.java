package database.tables;

import mainClasses.Customer;
import com.google.gson.Gson;
import database.DB_Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EditCustomersTable {

    public void addCustomerFromJSON(String json) throws ClassNotFoundException {
        Customer customer = jsonToCustomer(json);
        addNewCustomer(customer);
    }

    public Customer jsonToCustomer(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Customer.class);
    }

    public String customerToJSON(Customer customer) {
        Gson gson = new Gson();
        return gson.toJson(customer, Customer.class);
    }



    public void createCustomersTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE customers ("
                + "customer_id INTEGER not NULL AUTO_INCREMENT, "
                + "name VARCHAR(50) not NULL, "
                + "email VARCHAR(50) not NULL UNIQUE, "
                + "credit_card_details VARCHAR(100), "
                + "balance FLOAT DEFAULT 0.0, " // Balance as FLOAT
                + "PRIMARY KEY (customer_id))";


        stmt.execute(query);
        stmt.close();
    }

    public void addNewCustomer(Customer customer) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();
            System.out.println("CUSTOMER BEING ADDED...\n"
                    + "Name: " + customer.getName()
                    + ", Email: " + customer.getEmail()
                    + ", Credit Card Details: " + customer.getCreditCardDetails()
                    + ", Balance: " + customer.getBalance());

            String insertQuery = "INSERT INTO customers (name, email, credit_card_details, balance) "
                    + "VALUES ("
                    + "'" + customer.getName() + "',"
                    + "'" + customer.getEmail() + "',"
                    + "'" + customer.getCreditCardDetails() + "',"
                    + customer.getBalance()
                    + ")";

            System.out.println("Insert query: " + insertQuery);

            stmt.executeUpdate(insertQuery);

            System.out.println("# The customer was successfully added to the database.");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditCustomersTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    public Customer getCustomerByEmail(String email) throws SQLException, ClassNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Email parameter is null or empty.");
            return null;
        }

        String query = "SELECT * FROM customers WHERE LOWER(email) = LOWER(?)";

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email.trim());
            System.out.println("Executing query: " + pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String json = DB_Connection.getResultsToJSON(rs);
                    System.out.println("Customer found: " + json);
                    Gson gson = new Gson();
                    return gson.fromJson(json, Customer.class);
                } else {
                    System.out.println("No customer found with email: " + email.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception occurred while fetching customer by email: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("An exception occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public float getCustomerBalance(int customerId) throws ClassNotFoundException, SQLException {
        float balance = 0.0f;
        String query = "SELECT balance FROM customers WHERE customer_id = ?";
        try (Connection con = DB_Connection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    balance = rs.getFloat("balance");
                } else {
                    throw new SQLException("Customer not found with ID: " + customerId);
                }
            }
        }
        return balance;
    }


}

package database.tables;

import mainClasses.Customer;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles database operations for Customer objects.
 */
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

    public void updateCustomerEmail(int customerId, String email) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update = "UPDATE customers SET email='" + email + "' WHERE customer_id=" + customerId;
        stmt.executeUpdate(update);
        stmt.close();
    }

    public Customer databaseToCustomer(int customerId) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE customer_id=" + customerId);
            if (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                return gson.fromJson(json, Customer.class);
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        } finally {
            stmt.close();
        }
        return null;
    }

    public void createCustomersTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE customers ("
                + "customer_id INTEGER not NULL AUTO_INCREMENT, "
                + "name VARCHAR(50) not NULL, "
                + "email VARCHAR(50) not NULL UNIQUE, "
                + "credit_card_details VARCHAR(100), "
                + "PRIMARY KEY (customer_id))";
        stmt.execute(query);
        stmt.close();
    }

    public void addNewCustomer(Customer customer) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();
            System.out.println("CUSTOMER BEING ADDED...\n" + "name " + customer.getName() + "email " + customer.getEmail() + "credit card details " + customer.getCreditCardDetails());
            String insertQuery = "INSERT INTO customers (name, email, credit_card_details) "
                    + "VALUES ("
                    + "'" + customer.getName() + "',"
                    + "'" + customer.getEmail() + "',"
                    + "'" + customer.getCreditCardDetails() + "'"
                    + ")";
            System.out.println("Insert query"+ insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The customer was successfully added to the database.");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditCustomersTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers");
            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                customers.add(gson.fromJson(json, Customer.class));
            }
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        } finally {
            stmt.close();
        }
        return customers;
    }

    public void deleteCustomer(int customerId) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();
            String deleteQuery = "DELETE FROM customers WHERE customer_id=" + customerId;
            stmt.executeUpdate(deleteQuery);
            System.out.println("# The customer was successfully deleted from the database.");
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditCustomersTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

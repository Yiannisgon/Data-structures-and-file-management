package servlets;


import database.tables.EditCustomersTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainClasses.Customer;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "CreateCustomer", urlPatterns = {"/CreateCustomer"})
public class CreateCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Opened CreateCustomer");
        response.setContentType("application/json;charset=UTF-8");

        // Read from request
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            response.setStatus(400); // Bad request
            response.getWriter().write("Error reading input: " + e.getMessage());
            return;
        }

        String requestData = sb.toString();
        System.out.println("Received JSON: " + requestData);

        try (PrintWriter out = response.getWriter()) {
            EditCustomersTable ect = new EditCustomersTable();
            Gson gson = new Gson();

            // Convert JSON string to Customer object
            Customer newCustomer = gson.fromJson(requestData, Customer.class);

            // Validate fields
            if (newCustomer.getName() == null || newCustomer.getName().isEmpty() ||
                    newCustomer.getEmail() == null || newCustomer.getEmail().isEmpty() ||
                    newCustomer.getCreditCardDetails() == null || newCustomer.getCreditCardDetails().isEmpty() ||
                    newCustomer.getBalance() < 0) { // Ensure balance is not negative
                System.err.println("Validation failed: Missing or invalid fields.");
                response.setStatus(400); // Bad request
                out.write("Missing required fields: name, email, credit_card_details, or invalid balance.");
                return;
            }

            System.out.println("Parsed Customer: " +
                    "Name: " + newCustomer.getName() +
                    ", Email: " + newCustomer.getEmail() +
                    ", Credit Card: " + newCustomer.getCreditCardDetails() +
                    ", Balance: " + newCustomer.getBalance());

            // Add the new customer to the database
            ect.addNewCustomer(newCustomer);

            response.setStatus(200); // Set success status
            out.println(gson.toJson(newCustomer)); // Optionally return the added customer as JSON
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CreateCustomer.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Set error status code
            response.getWriter().write("An error occurred: " + ex.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            response.setStatus(500);
            response.getWriter().write("Unexpected error occurred: " + e.getMessage());
        }
    }
}

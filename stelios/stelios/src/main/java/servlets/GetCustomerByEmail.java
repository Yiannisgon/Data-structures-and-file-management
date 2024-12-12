package servlets;

import database.tables.EditCustomersTable;
import mainClasses.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "GetCustomerByEmail", urlPatterns = {"/GetCustomerByEmail"})
public class GetCustomerByEmail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the email parameter from the request
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            // Respond with an error if email is missing
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Email parameter is required\"}");
            return;
        }

        try {
            // Use EditCustomersTable to fetch the customer by email
            EditCustomersTable ect = new EditCustomersTable();
            Customer customer = ect.getCustomerByEmail(email);

            if (customer != null) {
                // If the customer exists, return their details as JSON
                response.getWriter().write(ect.customerToJSON(customer));
            } else {
                // If no customer is found, respond with a 404 status
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Customer not found\"}");
            }

        } catch (Exception e) {
            // Handle any exceptions and respond with a 500 status
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An error occurred: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}

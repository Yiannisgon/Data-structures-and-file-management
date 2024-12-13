package servlets;

import database.tables.EditTicketsTable;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Servlet to get the total revenue for all ticket types.
 */
@WebServlet("/TotalRevenueServlet")  // This is the URL pattern for this servlet
public class TotalRevenueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // Create an instance of EditTicketsTable to access the database method
            EditTicketsTable editTicketsTable = new EditTicketsTable();

            // Get the total revenue for all ticket types (returns a Map)
            Map<String, Float> revenueMap = editTicketsTable.getTotalRevenueForAllTypes();

            // Convert the revenue map to JSON using Gson
            String json = new Gson().toJson(revenueMap);

            // Send the JSON response
            out.println(json);

            response.setStatus(200);  // Set the status to 200 (OK)
        } catch (SQLException | ClassNotFoundException ex) {
            // Log the error and send a 500 status in case of failure
            Logger.getLogger(TotalRevenueServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);  // Internal Server Error
        }
    }
}

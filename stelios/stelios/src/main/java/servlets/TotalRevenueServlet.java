package servlets;

import database.tables.EditReservationsTable;
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
@WebServlet(name = "TotalRevenueServlet", urlPatterns = {"/TotalRevenueServlet"})
public class TotalRevenueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            EditTicketsTable editTicketsTable = new EditTicketsTable();

            // Get the total revenue for all ticket types
            Map<String, Float> revenueMap = editTicketsTable.getTotalRevenueForAllTypes();

            // Add the refund income to the response
            float totalRefundTax = EditReservationsTable.getTotalRefundTax(); // Get the global refund tax tracker
            revenueMap.put("refundIncome", totalRefundTax);

            // Convert the map to JSON
            String json = new Gson().toJson(revenueMap);

            // Log and send the response
            System.out.println("Total Revenue JSON: " + json);
            out.println(json);
            response.setStatus(200);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(TotalRevenueServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Failed to fetch total revenue.\"}");
        }
    }
}

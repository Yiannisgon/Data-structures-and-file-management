package servlets;

import database.DB_Connection;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/petkeeper-info")
public class PetKeeperInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.err.println("I AM HEREEE ");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();

        try {
            Connection connection = DB_Connection.getConnection();

            // Example SQL queries to get the required information
            // You need to write the actual SQL query according to your database schema
            String sqlReservations = "SELECT COUNT(*) AS reservationCount FROM Bookings WHERE status = 'finished'";
            String sqlTotalHostingDays = "SELECT SUM(duration) AS totalDays FROM Bookings WHERE status = 'finished'";
            String sqlReviews = "SELECT comment FROM Reviews WHERE status = 'finished'";

            // Get reservations count
            try (PreparedStatement pstmtReservations = connection.prepareStatement(sqlReservations);
                 ResultSet rsReservations = pstmtReservations.executeQuery()) {
                if (rsReservations.next()) {
                    jsonResponse.put("reservations", rsReservations.getInt("reservationCount"));
                }
            }

            // Get total hosting days
            try (PreparedStatement pstmtTotalDays = connection.prepareStatement(sqlTotalHostingDays);
                 ResultSet rsTotalDays = pstmtTotalDays.executeQuery()) {
                if (rsTotalDays.next()) {
                    jsonResponse.put("totalHostingDays", rsTotalDays.getInt("totalDays"));
                }
            }

            // Get reviews
            JSONArray reviewsArray = new JSONArray();
            try (PreparedStatement pstmtReviews = connection.prepareStatement(sqlReviews);
                 ResultSet rsReviews = pstmtReviews.executeQuery()) {
                while (rsReviews.next()) {
                    reviewsArray.put(rsReviews.getString("comment"));
                }
                jsonResponse.put("reviews", reviewsArray);
            }

            connection.close();
        } catch (SQLException e) {
            // Handle potential SQL exception
            e.printStackTrace();
            // Set the error message in case of exception
            jsonResponse.put("error", "Database access error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}

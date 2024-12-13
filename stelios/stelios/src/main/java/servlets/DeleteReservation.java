package servlets;

import database.tables.EditReservationsTable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet to delete a reservation from the database.
 */
@WebServlet(name = "DeleteReservation", urlPatterns = {"/DeleteReservation"})
public class DeleteReservation extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reservationId = request.getParameter("reservation_id");

        try {
            EditReservationsTable editReservationsTable = new EditReservationsTable();

            if (reservationId != null && !reservationId.isEmpty()) {
                editReservationsTable.deleteReservationById(reservationId);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Reservation with ID " + reservationId + " successfully deleted.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid reservation ID.");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DeleteReservation.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("An error occurred while deleting the reservation.");
        }
    }
}

package servlets;

import database.tables.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mainClasses.Booking;
import mainClasses.PetKeeper;
import com.google.gson.Gson;
import mainClasses.PetOwner;

import java.util.List;
/**
 * @author ioannis
 * Servlet to delete a user from database
 */
public class DeleteUser extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String userId = request.getParameter("id");
        String type = request.getParameter("type");
        String username = request.getParameter("username");;

        try (PrintWriter out = response.getWriter()) {

            EditBookingsTable book = new EditBookingsTable();
            EditReviewsTable rev = new EditReviewsTable();
            EditMessagesTable mes = new EditMessagesTable();

            if (type.equals("keeper")) {
                EditPetKeepersTable eut = new EditPetKeepersTable();

                try {
                    List<Booking> bookings = book.databaseToBookings("0",userId,type);
                    for (Booking booking : bookings) {//Error
                        mes.deleteMessagesByBookingId(booking.getBorrowing_id());
                    }
                    //Error if Messages are not deleted
                    book.deleteBookings(userId);
                    rev.deleteReview(userId,type);
                    eut.deletePetKeeper(userId);


                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DeleteUser.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("Class Not Found Error: " + ex.getMessage());
                    // Handle ClassNotFound specific error here
                }

            } else if (type.equals("owner")) {
                EditPetOwnersTable eot = new EditPetOwnersTable();
                EditPetsTable ept = new EditPetsTable();
                // Add your logic for pet owners here

                try {
                    List<Booking> bookings = book.databaseToBookings("0",userId,type);
                    for (Booking booking : bookings) {//Error
                        mes.deleteMessagesByBookingId(booking.getBorrowing_id());
                    }
                    //Error if Messages are not deleted
                    book.deleteBookings(userId);
                    rev.deleteReview(userId,type);
                    ept.deletePetByOwnerId(userId);
                    eot.deletePetOwner(userId);


                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DeleteUser.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("Class Not Found Error: " + ex.getMessage());
                    // Handle ClassNotFound specific error here
                }
            } else {
                out.println("Error in deleteUser servlet");
                // Handle invalid type here
            }

            // If all went well, set success status
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            // Catch any other exceptions and perform general error handling
            Logger.getLogger(DeleteUser.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("General Error: " + ex.getMessage());
        }
    }

}

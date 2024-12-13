package mainClasses;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class Reservation {
    private int reservationId;

    @SerializedName("customerID")
    private int customerId;

    @SerializedName("eventID")
    private int eventId;

    private int ticketCount;
    private float paymentAmount;
    private Timestamp reservationDate;

    // Added field for ticket type
    private String ticketType;

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Timestamp getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Timestamp reservationDate) {
        this.reservationDate = reservationDate;
    }

    // Getter and Setter for ticketType
    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", customerId=" + customerId +
                ", eventId=" + eventId +
                ", ticketCount=" + ticketCount +
                ", paymentAmount=" + paymentAmount +
                ", reservationDate=" + reservationDate +
                ", ticketType='" + ticketType + '\'' +
                '}';
    }
}

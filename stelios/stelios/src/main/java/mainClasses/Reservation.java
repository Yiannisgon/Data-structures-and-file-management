package mainClasses;

import java.sql.Timestamp;

public class Reservation {
    int reservationId, customerId, eventId, TicketCount, PaymentAmount;
    Timestamp ReservationDate;

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
        return TicketCount;
    }

    public void setTicketCount(int ticketCount) {
        TicketCount = ticketCount;
    }

    public int getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public Timestamp getReservationDate() {
        return ReservationDate;
    }

    public void setReservationDate(Timestamp reservationDate) {
        ReservationDate = reservationDate;
    }
}

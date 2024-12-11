package mainClasses;

public class Ticket {
    int Ticket_id, Availability;
    Float Price;
    String Type;

    public int getTicket_id() {
        return Ticket_id;
    }

    public void setTicket_id(int ticket_id) {
        Ticket_id = ticket_id;
    }

    public int getAvailability() {
        return Availability;
    }

    public void setAvailability(int availability) {
        Availability = availability;
    }

    public Float getPrice() {
        return Price;
    }

    public void setPrice(Float price) {
        Price = price;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}

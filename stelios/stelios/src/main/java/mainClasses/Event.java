package mainClasses;

public class Event {

    // NOTE : The java.sql package has three date/time types:
    //java.sql.Date - A date only (no time part)
    //java.sql.Time - A time only (no date part)
    //java.sql.Timestamp - Both date and time
    // -Y
    String name;
    int event_id, Capacity;
    java.sql.Date Date;
    java.sql.Time Time;
    String Type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.sql.Date date) {
        Date = date;
    }

    public java.sql.Time getTime() {
        return Time;
    }

    public void setTime(java.sql.Time time) {
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}

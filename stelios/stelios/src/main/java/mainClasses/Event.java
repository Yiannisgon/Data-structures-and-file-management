package mainClasses;

import java.sql.Date;
import java.sql.Time;

public class Event {
    private String name;
    private int capacity; // Renamed to match JSON key "capacity"
    private Date date; // Matches JSON key "date"
    private Time time; // Matches JSON key "time"
    private String type; // Renamed to match JSON key "type"
    private int event_id; // Retained as-is, assuming it's for internal use and not part of the JSON

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEventId() {
        return event_id;
    }

    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
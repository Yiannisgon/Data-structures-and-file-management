SELECT
    e.EventID,
    e.Name AS EventName,
    t.Type AS TicketType,
    t.Availability AS TotalSeats,
    COALESCE(SUM(r.TicketCount), 0) AS ReservedSeats,
    (t.Availability - COALESCE(SUM(r.TicketCount), 0)) AS AvailableSeats
FROM
    Events e
JOIN Tickets t ON e.EventID = t.EventID
LEFT JOIN Reservations r ON t.EventID = r.EventID AND t.Type = r.TicketType
GROUP BY
    e.EventID, e.Name, t.Type, t.Availability;

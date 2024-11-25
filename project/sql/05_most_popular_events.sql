SELECT
    e.EventID,
    e.Name AS EventName,
    COUNT(r.ReservationID) AS TotalReservations
FROM
    Events e
JOIN Reservations r ON e.EventID = r.EventID
GROUP BY
    e.EventID, e.Name
ORDER BY
    TotalReservations DESC
LIMIT 1;

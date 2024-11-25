SELECT
    r.ReservationID,
    c.Name AS CustomerName,
    e.Name AS EventName,
    r.TicketCount,
    r.ReservationDate,
    r.PaymentAmount
FROM
    Reservations r
JOIN Customers c ON r.CustomerID = c.CustomerID
JOIN Events e ON r.EventID = e.EventID
WHERE
    r.ReservationDate BETWEEN '2024-12-01' AND '2024-12-31' -- same here
ORDER BY
    r.ReservationDate ASC;

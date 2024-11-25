SELECT
    e.EventID,
    e.Name AS EventName,
    t.Type AS TicketType,
    SUM(r.PaymentAmount) AS TotalRevenue
FROM
    Events e
JOIN Tickets t ON e.EventID = t.EventID
JOIN Reservations r ON e.EventID = r.EventID AND t.Type = r.TicketType
WHERE
    t.Type = 'VIP'
GROUP BY
    e.EventID, e.Name, t.Type;

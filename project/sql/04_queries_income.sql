SELECT
    e.EventID,
    e.Name AS EventName,
    SUM(r.PaymentAmount) AS TotalRevenue
FROM
    Events e
JOIN Reservations r ON e.EventID = r.EventID
GROUP BY
    e.EventID, e.Name;

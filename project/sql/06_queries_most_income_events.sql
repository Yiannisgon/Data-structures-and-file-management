SELECT
    e.EventID,
    e.Name AS EventName,
    SUM(r.PaymentAmount) AS TotalRevenue
FROM
    Events e
JOIN Reservations r ON e.EventID = r.EventID
WHERE
    r.ReservationDate BETWEEN '2024-12-01' AND '2024-12-31' --havent implemented variable specific time period 
GROUP BY
    e.EventID, e.Name
ORDER BY
    TotalRevenue DESC
LIMIT 1;

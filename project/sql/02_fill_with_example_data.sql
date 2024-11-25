-- Insert example data into Events table
INSERT INTO Events (Name, Date, Time, Type, Capacity) VALUES
('Rock Concert', '2024-12-15', '2024-12-15 20:00:00', 'Concert', 500),
('Classical Music Night', '2024-12-20', '2024-12-20 19:00:00', 'Concert', 300),
('Stand-Up Comedy', '2024-12-25', '2024-12-25 21:00:00', 'Comedy', 200),
('Jazz Evening', '2024-12-18', '2024-12-18 20:30:00', 'Concert', 250),
('Tech Conference', '2024-12-10', '2024-12-10 09:00:00', 'Conference', 600);

-- Insert example data into Customers table
INSERT INTO Customers (Name, Email, CreditCardDetails) VALUES
('Alice Johnson', 'alice.johnson@example.com', '1234-5678-9012-3456'),
('Bob Smith', 'bob.smith@example.com', '2345-6789-0123-4567'),
('Charlie Brown', 'charlie.brown@example.com', '3456-7890-1234-5678'),
('Diana Prince', 'diana.prince@example.com', '4567-8901-2345-6789'),
('Ethan Hunt', 'ethan.hunt@example.com', '5678-9012-3456-7890'),
('Fiona Gallagher', 'fiona.gallagher@example.com', '6789-0123-4567-8901'),
('George Clooney', 'george.clooney@example.com', '7890-1234-5678-9012'),
('Hannah Montana', 'hannah.montana@example.com', '8901-2345-6789-0123'),
('Ian McKellen', 'ian.mckellen@example.com', '9012-3456-7890-1234'),
('Jennifer Aniston', 'jennifer.aniston@example.com', '0123-4567-8901-2345'),
('Kevin Hart', 'kevin.hart@example.com', '2345-6789-0123-4567'),
('Laura Croft', 'laura.croft@example.com', '3456-7890-1234-5678'),
('Michael Jordan', 'michael.jordan@example.com', '4567-8901-2345-6789'),
('Nina Dobrev', 'nina.dobrev@example.com', '5678-9012-3456-7890'),
('Olivia Wilde', 'olivia.wilde@example.com', '6789-0123-4567-8901'),
('Peter Parker', 'peter.parker@example.com', '7890-1234-5678-9012'),
('Quentin Tarantino', 'quentin.tarantino@example.com', '8901-2345-6789-0123'),
('Rachel Green', 'rachel.green@example.com', '9012-3456-7890-1234'),
('Sam Winchester', 'sam.winchester@example.com', '0123-4567-8901-2345'),
('Tony Stark', 'tony.stark@example.com', '2345-6789-0123-4567'),
('Uma Thurman', 'uma.thurman@example.com', '3456-7890-1234-5678'),
('Violet Baudelaire', 'violet.baudelaire@example.com', '4567-8901-2345-6789'),
('Walter White', 'walter.white@example.com', '5678-9012-3456-7890'),
('Xena Warrior', 'xena.warrior@example.com', '6789-0123-4567-8901'),
('Yara Shahidi', 'yara.shahidi@example.com', '7890-1234-5678-9012'),
('Zack Morris', 'zack.morris@example.com', '8901-2345-6789-0123'),
('Abigail Adams', 'abigail.adams@example.com', '9012-3456-7890-1234'),
('Brian O’Conner', 'brian.oconnor@example.com', '0123-4567-8901-2345'),
('Claire Redfield', 'claire.redfield@example.com', '2345-6789-0123-4567'),
('Damian Wayne', 'damian.wayne@example.com', '3456-7890-1234-5678');

-- Insert example data into Tickets table
INSERT INTO Tickets (EventID, Type, Price, Availability) VALUES
(1, 'VIP', 120.00, 100),
(1, 'General', 60.00, 400),
(2, 'VIP', 90.00, 50),
(2, 'General', 50.00, 250),
(3, 'VIP', 75.00, 40),
(3, 'General', 35.00, 160),
(4, 'VIP', 100.00, 60),
(4, 'General', 50.00, 190),
(5, 'VIP', 150.00, 200),
(5, 'General', 80.00, 400);

-- Insert example data into Reservations table
INSERT INTO Reservations (CustomerID, EventID, TicketCount, ReservationDate, PaymentAmount) VALUES
(1, 1, 2, '2024-11-24 10:00:00', 240.00),
(2, 1, 4, '2024-11-24 11:00:00', 240.00),
(3, 2, 3, '2024-11-25 12:00:00', 270.00),
(4, 2, 2, '2024-11-26 14:00:00', 180.00),
(5, 3, 1, '2024-11-27 09:00:00', 75.00),
(6, 3, 2, '2024-11-28 16:00:00', 150.00),
(7, 4, 3, '2024-11-29 18:30:00', 300.00),
(8, 4, 5, '2024-11-30 20:00:00', 500.00),
(9, 5, 2, '2024-12-01 15:00:00', 300.00),
(10, 5, 1, '2024-12-02 13:00:00', 150.00),
(11, 1, 2, '2024-12-03 10:00:00', 240.00),
(12, 2, 4, '2024-12-04 11:30:00', 360.00),
(13, 3, 1, '2024-12-05 12:00:00', 75.00),
(14, 4, 3, '2024-12-06 14:30:00', 300.00),
(15, 5, 2, '2024-12-07 15:00:00', 300.00);

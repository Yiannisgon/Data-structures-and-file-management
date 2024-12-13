function onEventChange() {
    const eventID = document.getElementById("eventID").value;
    if (!eventID) {
        document.getElementById("selectedEventDateTime").innerText = "Please select an event.";
        return;
    }

    fetch(`GetEventDetails?event_id=${eventID}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch event details: ${response.status}`);
            }
            return response.json();
        })
        .then(event => {
            if (event.date && event.time) {
                // Combine date and time for display
                document.getElementById("selectedEventDateTime").innerText = `${event.date} ${event.time}`;
            } else {
                document.getElementById("selectedEventDateTime").innerText = "Event details unavailable.";
            }
        })
        .catch(error => {
            console.error("Error fetching event details:", error);
            document.getElementById("selectedEventDateTime").innerText = "Error loading event details.";
        });
}

function onTicketTypeChange() {
    const eventID = document.getElementById("eventID").value;
    const ticketType = document.getElementById("ticketType").value;

    if (!eventID || !ticketType) {
        alert("Please select an event and ticket type.");
        return;
    }

    fetch(`GetTicketDetails?event_id=${eventID}&type=${encodeURIComponent(ticketType)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch ticket details: ${response.status}`);
            }
            return response.json();
        })
        .then(ticket => {
            if (ticket.availability >= 0 && ticket.price) {
                document.getElementById("ticketCount").max = ticket.availability; // Set max tickets
                document.getElementById("ticketCount").value = ""; // Reset count
                document.getElementById("totalPrice").innerText = "0.00"; // Reset price
                document.getElementById("ticketCount").dataset.price = ticket.price; // Store ticket price
            } else {
                alert("No tickets available for the selected type.");
            }
        })
        .catch(error => {
            console.error("Error fetching ticket details:", error);
            alert("Failed to load ticket details. Please try again.");
        });
}

function updatePrice() {
    const ticketCount = parseInt(document.getElementById("ticketCount").value) || 0;
    const pricePerTicket = parseFloat(document.getElementById("ticketCount").dataset.price) || 0;

    const totalPrice = ticketCount * pricePerTicket;
    document.getElementById("totalPrice").innerText = totalPrice.toFixed(2);
}

function submitReservationAndCreate() {
    const ticketCount = parseInt(document.getElementById("ticketCount").value);
    const pricePerTicket = parseFloat(document.getElementById("ticketCount").dataset.price);
    const totalPrice = ticketCount * pricePerTicket;

    const formData = {
        customerID: null, // Placeholder, to be updated after fetching customer details
        eventID: parseInt(document.getElementById("eventID").value),
        ticketCount: ticketCount,
        paymentAmount: totalPrice, // Ensure numeric value
        reservationDate: new Date().toISOString(), // Ensure proper ISO format
        ticketType: document.getElementById("ticketType").value,
    };

    if (!formData.ticketCount || formData.ticketCount <= 0) {
        alert("Please enter a valid ticket count.");
        return;
    }

    if (!formData.eventID || !formData.ticketType) {
        alert("Please select a valid event and ticket type.");
        return;
    }

    const customerEmail = document.getElementById("customerEmail").value.trim();
    if (!customerEmail) {
        alert("Customer email is required.");
        return;
    }

    fetch(`GetCustomerByEmail?email=${encodeURIComponent(customerEmail)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch customer details: ${response.status}`);
            }
            return response.json();
        })
        .then(customer => {
            if (customer.balance < totalPrice) {
                document.getElementById("balanceError").innerText = "Insufficient balance.";
                return;
            }

            formData.customerID = customer.customer_id; // Assign customer ID

            // Send reservation JSON to backend
            return fetch("CreateReservation", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });
        })
        .then(response => {
            if (response && response.ok) {
                alert("Reservation successfully created.");
            } else if (response) {
                response.text().then(text => alert("Error creating reservation: " + text));
            }
        })
        .catch(error => {
            console.error("Error during reservation process:", error);
            alert("An error occurred during the reservation process.");
        });
}

function populateEventDropdown() {
    fetch('GetEvents')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch events: ${response.status}`);
            }
            return response.json();
        })
        .then(events => {
            const eventDropdown = document.getElementById('eventID');
            eventDropdown.innerHTML = '<option value="" disabled selected>Select an event</option>';
            events.forEach(event => {
                if (event.event_id && event.name && event.date && event.time) {
                    const option = document.createElement('option');
                    option.value = event.event_id;
                    option.textContent = `${event.name} (${event.date} at ${event.time})`;
                    eventDropdown.appendChild(option);
                }
            });
        })
        .catch(error => console.error('Error fetching events:', error));
}

// Initialize event dropdown on page load
document.addEventListener("DOMContentLoaded", populateEventDropdown);


//new new
// Fetch and display reservations based on the customer email
function getReservationsByEmail() {
    const customerEmail = document.getElementById("cancelReservationEmail").value.trim();

    if (!customerEmail) {
        alert("Please enter a valid email.");
        return;
    }

    fetch(`GetReservationsByEmail?email=${encodeURIComponent(customerEmail)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch reservations: ${response.status}`);
            }
            return response.json();
        })
        .then(reservations => {
            if (reservations.length === 0) {
                document.getElementById("reservationListContainer").innerHTML = "No reservations found for this email.";
            } else {
                let reservationListContent = '';
                reservations.forEach(reservation => {
                    reservationListContent += createReservationTable(reservation);
                });
                document.getElementById("reservationListContainer").innerHTML = reservationListContent;
            }
        })
        .catch(error => {
            console.error("Error fetching reservations:", error);
            document.getElementById("reservationListContainer").innerHTML = "Error loading reservations.";
        });
}

// Create the table to display reservations
function createReservationTable(reservation) {
    let html = '<table>';
    html += `<tr><th colspan="2">Reservation ID: ${reservation.reservationId}</th></tr>`;

    ['customerId', 'eventId', 'ticketCount', 'paymentAmount', 'reservationDate', 'ticketType'].forEach(function (key) {
        if (reservation[key]) {
            html += `<tr><td>${key.replace(/([A-Z])/g, ' $1').trim()}</td><td>${reservation[key]}</td></tr>`;
        }
    });

    html += `<tr><td colspan="2"><button onclick="cancelReservation(${reservation.reservationId})">Cancel Reservation</button></td></tr>`;
    html += '</table><br>';
    return html;
}

// Handle the cancellation of a reservation
function cancelReservation(reservationId) {
    if (!reservationId || isNaN(reservationId)) {
        alert("Invalid Reservation ID!");
        return;
    }

    fetch(`DeleteReservation?reservation_id=${reservationId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to cancel reservation: ${response.status}`);
            }
            return response.text();
        })
        .then(message => {
            alert(message);
            getReservationsByEmail(); // Refresh the list after cancellation
        })
        .catch(error => {
            console.error("Error canceling reservation:", error);
            alert("An error occurred while canceling the reservation.");
        });
}

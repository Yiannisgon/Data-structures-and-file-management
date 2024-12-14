// JavaScript file to handle fetching and displaying event statistics

// Fetch and display revenue for the selected event
function fetchRevenuePerEvent() {
    const eventID = document.getElementById("revenue-event-dropdown").value;
    if (!eventID) {
        alert("Please select an event.");
        return;
    }

    fetch(`GetRevenuePerEvent?event_id=${eventID}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch revenue: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById('revenue-per-event');
            if (data.revenue !== undefined) {
                container.innerHTML = `<p>Total Revenue: $${data.revenue.toFixed(2)}</p>`;
            } else {
                container.innerHTML = "Revenue data unavailable.";
            }
        })
        .catch(error => {
            console.error('Error fetching revenue per event:', error);
            const container = document.getElementById('revenue-per-event');
            container.innerHTML = "Error loading revenue data.";
        });
}

// Populate dropdown with all events
function populateEventDropdown() {
    fetch('GetEvents')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch events: ${response.status}`);
            }
            return response.json();
        })
        .then(events => {
            const eventDropdown = document.getElementById('event-dropdown');
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

// Populate dropdown for revenue events
function populateRevenueEventDropdown() {
    fetch('GetEvents')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch events: ${response.status}`);
            }
            return response.json();
        })
        .then(events => {
            const revenueDropdown = document.getElementById('revenue-event-dropdown');
            revenueDropdown.innerHTML = '<option value="" disabled selected>Select an event</option>';
            events.forEach(event => {
                if (event.event_id && event.name && event.date && event.time) {
                    const option = document.createElement('option');
                    option.value = event.event_id;
                    option.textContent = `${event.name} (${event.date} at ${event.time})`;
                    revenueDropdown.appendChild(option);
                }
            });
        })
        .catch(error => console.error('Error fetching events for revenue:', error));
}

// Fetch and display available and reserved seats for the selected event
function fetchAvailableAndReservedSeats() {
    const eventID = document.getElementById("event-dropdown").value;
    if (!eventID) {
        alert("Please select an event.");
        return;
    }

    fetch(`GetAvailableAndReservedSeats?event_id=${eventID}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch available and reserved seats: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById('available-reserved-seats');
            if (data.availableSeats !== undefined && data.reservedSeats !== undefined) {
                container.innerHTML = `<p>Available Seats: ${data.availableSeats}</p><p>Reserved Seats: ${data.reservedSeats}</p>`;
            } else {
                container.innerHTML = "Data unavailable.";
            }
        })
        .catch(error => {
            console.error('Error fetching available and reserved seats:', error);
            const container = document.getElementById('available-reserved-seats');
            container.innerHTML = "Error loading data.";
        });
}


function fetchMostTicketsReservedEvent() {
    fetch('GetMostTicketsReservedEvent')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch most tickets reserved event: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById('most-tickets-reserved-event');
            if (data.eventName && data.ticketCount) {
                container.innerHTML = `<p>Event Name: ${data.eventName}</p><p>Tickets Reserved: ${data.ticketCount}</p>`;
            } else {
                container.innerHTML = "No data available.";
            }
        })
        .catch(error => {
            console.error('Error fetching most tickets reserved event:', error);
            const container = document.getElementById('most-tickets-reserved-event');
            container.innerHTML = "Error loading data.";
        });
}

function fetchMostProfitableEvent() {
    const startDate = document.getElementById("start-date").value;
    const endDate = document.getElementById("end-date").value;

    if (!startDate || !endDate) {
        alert("Please select both start and end dates.");
        return;
    }

    fetch(`GetMostProfitableEvent?start_date=${startDate}&end_date=${endDate}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch most profitable event: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById("most-profitable-event");
            if (data.eventName && data.revenue) {
                container.innerHTML = `<p>Event Name: ${data.eventName}</p><p>Total Revenue: $${data.revenue.toFixed(2)}</p>`;
            } else {
                container.innerHTML = "No data available for the selected time range.";
            }
        })
        .catch(error => {
            console.error("Error fetching most profitable event:", error);
            const container = document.getElementById("most-profitable-event");
            container.innerHTML = "Error loading data.";
        });
}

function fetchReservationsByTimePeriod() {
    const startDate = document.getElementById("reservation-start-date").value;
    const endDate = document.getElementById("reservation-end-date").value;

    if (!startDate || !endDate) {
        alert("Please select both start and end dates.");
        return;
    }

    fetch(`GetReservationsByTimePeriod?start_date=${startDate}&end_date=${endDate}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch reservations by time period: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById("reservations-by-time-period");
            if (data.length > 0) {
                let html = "<table><tr><th>Reservation ID</th><th>Event</th><th>Customer</th><th>Tickets</th><th>Payment</th><th>Date</th></tr>";
                data.forEach(reservation => {
                    html += `<tr>
                                <td>${reservation.reservationId}</td>
                                <td>${reservation.eventName}</td>
                                <td>${reservation.customerName}</td>
                                <td>${reservation.ticketCount}</td>
                                <td>$${reservation.paymentAmount.toFixed(2)}</td>
                                <td>${reservation.reservationDate}</td>
                             </tr>`;
                });
                html += "</table>";
                container.innerHTML = html;
            } else {
                container.innerHTML = "No reservations found for the selected time period.";
            }
        })
        .catch(error => {
            console.error("Error fetching reservations by time period:", error);
            const container = document.getElementById("reservations-by-time-period");
            container.innerHTML = "Error loading data.";
        });
}

function populateEventNameDropdown() {
    fetch('GetEvents')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch events: ${response.status}`);
            }
            return response.json();
        })
        .then(events => {
            const eventDropdown = document.getElementById('event-name');
            eventDropdown.innerHTML = '<option value="" disabled selected>Select an event</option>';
            events.forEach(event => {
                if (event.name) {
                    const option = document.createElement('option');
                    option.value = event.name;
                    option.textContent = event.name;
                    eventDropdown.appendChild(option);
                }
            });
        })
        .catch(error => console.error('Error fetching events:', error));
}


function fetchEventRevenueByTicketType() {
    const eventName = document.getElementById("event-name").value;
    const ticketType = document.getElementById("ticket-type-event").value;

    if (!eventName || !ticketType) {
        alert("Please select both an event and a ticket type.");
        return;
    }

    fetch(`GetEventRevenueByTicketType?event_name=${encodeURIComponent(eventName)}&ticket_type=${ticketType}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch event revenue: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById("event-revenue-by-ticket-type");
            if (data.revenue !== undefined) {
                container.innerHTML = `<p>Total Revenue for ${ticketType} Tickets in "${eventName}": $${data.revenue.toFixed(2)}</p>`;
            } else {
                container.innerHTML = "Revenue data unavailable.";
            }
        })
        .catch(error => {
            console.error("Error fetching event revenue:", error);
            const container = document.getElementById("event-revenue-by-ticket-type");
            container.innerHTML = "Error loading data.";
        });
}

// Populate dropdowns on page load
document.addEventListener("DOMContentLoaded", () => {
    populateEventNameDropdown();
    populateEventDropdown();
    populateRevenueEventDropdown();
});

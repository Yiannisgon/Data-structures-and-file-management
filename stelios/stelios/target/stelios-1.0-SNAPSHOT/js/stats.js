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

// Initialize both dropdowns on page load
document.addEventListener("DOMContentLoaded", () => {
    populateEventDropdown();
    populateRevenueEventDropdown();
});

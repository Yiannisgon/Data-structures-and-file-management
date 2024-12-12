// Function to validate form inputs
function validateReservationForm(formData) {
    if (
        isNaN(formData.event_id) || formData.event_id <= 0 ||
        isNaN(formData.customer_id) || formData.customer_id <= 0 ||
        isNaN(formData.ticket_count) || formData.ticket_count <= 0 ||
        !formData.reservation_date ||
        isNaN(formData.payment_amount) || formData.payment_amount <= 0
    ) {
        alert("All fields must be filled correctly with valid values!");
        return false;
    }
    return true;
}

// Function to collect form data
function collectReservationData() {
    let reservationDateInput = document.getElementById("reservation-date").value;

    return {
        "event_id": parseInt(document.getElementById("event-id").value),
        "customer_id": parseInt(document.getElementById("customer-id").value),
        "ticket_count": parseInt(document.getElementById("ticket-count").value),
        "reservation_date": reservationDateInput.trim(),
        "payment_amount": parseFloat(document.getElementById("payment-amount").value)
    };
}

// Function to send reservation data to the server
function sendReservationData(formData) {
    // Initialize XMLHttpRequest
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'CreateReservation', true); // Adjusted endpoint for reservation creation
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    // Define what happens on successful data submission
    xhr.onload = function() {
        if (xhr.status === 200) {
            alert("Reservation successfully created");
            console.log("Response JSON:", xhr.responseText);
        } else {
            alert("An error occurred: " + xhr.responseText);
            console.error("Error response:", xhr.responseText);
        }
    };

    // Print the JSON being sent
    console.log("Sending JSON:", JSON.stringify(formData));

    // Send collected data as JSON
    xhr.send(JSON.stringify(formData));
}

// Main function to handle the reservation submission
function submitReservation() {
    const formData = collectReservationData();

    if (validateReservationForm(formData)) {
        sendReservationData(formData);
    }
}


// Function to fetch events from the server
function fetchEvents() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'GetAllEvents', true); // Adjust the endpoint if necessary
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    xhr.onload = function() {
        if (xhr.status === 200) {
            const events = JSON.parse(xhr.responseText);
            console.log("Fetched Events:", events);

            populateEventDropdown(events); // Call to populate dropdown
        } else {
            console.error("Failed to fetch events. Status:", xhr.status, "Response:", xhr.responseText);
        }
    };

    xhr.onerror = function() {
        console.error("Error during the request.");
    };

    xhr.send();
}

// Function to populate the event dropdown
function populateEventDropdown(events) {
    const eventDropdown = document.getElementById("event-dropdown");

    events.forEach(event => {
        const option = document.createElement("option");
        option.value = event.eventId; // Assuming eventId exists in the Event object
        option.textContent = `${event.name} (${event.date}, ${event.time})`;
        eventDropdown.appendChild(option);
    });
}

// Call the fetchEvents function when the page loads
document.addEventListener("DOMContentLoaded", fetchEvents);

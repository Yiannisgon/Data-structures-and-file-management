function submitReservationForm() {
    // Collect form data
    var formData = {
        "ticketCount": parseInt(document.getElementById("ticketCount").value),
        "reservationDate": document.getElementById("reservationDate").value.trim(),
        "paymentAmount": parseFloat(document.getElementById("paymentAmount").value),
        "customerID": parseInt(document.getElementById("customerID").value),
        "eventID": parseInt(document.getElementById("eventID").value)
    };

    // Validate form data
    if (!formData.ticketCount || !formData.reservationDate || isNaN(formData.paymentAmount) || !formData.customerID || !formData.eventID) {
        alert("All fields are required and must be valid!");
        return;
    }

    if (isNaN(formData.ticketCount) || formData.ticketCount <= 0) {
        alert("Ticket count must be a positive number!");
        return;
    }

    if (isNaN(formData.paymentAmount) || formData.paymentAmount <= 0) {
        alert("Payment amount must be a positive number!");
        return;
    }

    // Log the form data for debugging
    console.log("Form Data:", formData);

    // Initialize XMLHttpRequest for form submission
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
function closeModal() {
    // Close the modal
    const modal = document.getElementById('userListModal');
    modal.style.display = 'none';
}

function showAllEvents() {
    getEvents();
    toggleDisplay('eventListModal');
}

function getEvents() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse the JSON response
            var events = JSON.parse(xhr.responseText);

            // Create HTML content for each event using createTableEvent function
            var eventListContent = '';
            events.forEach(function(event) {
                eventListContent += createTableEvent(event);
            });

            // Update the #eventList div with the event data
            $("#eventList").html(eventListContent);
        } else if (xhr.status !== 200) {
            // Handle errors here, such as displaying a message to the user
            $("#eventList").html("Could not retrieve events data.");
        }
    };

    xhr.open('GET', 'GetEvents');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function createTableEvent(event) {
    var html = '<table>';
    html += '<tr><th colspan="2">' + event.name + '</th></tr>';

    // Add event_id if present
    if (event.hasOwnProperty('event_id') && event.event_id !== null) {
        html += '<tr><td>Event ID</td><td>' + event.event_id + '</td></tr>';
    }

    // Include keys and their values dynamically
    var includeKeys = ['name', 'capacity', 'date', 'time', 'type'];
    includeKeys.forEach(function(key) {
        if (event.hasOwnProperty(key) && event[key] !== null) {
            var value = event[key];
            html += '<tr><td>' + key.charAt(0).toUpperCase() + key.slice(1) + '</td><td>' + value + '</td></tr>';
        }
    });

    html += '</table>';
    return html;
}

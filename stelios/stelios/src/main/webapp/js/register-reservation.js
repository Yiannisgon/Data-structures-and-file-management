function submitReservationAndCreate() {
    // Collect form data
    var formData = {
        "ticketCount": parseInt(document.getElementById("ticketCount").value),
        "reservationDate": formatDateTime(document.getElementById("reservationDate").value),
        "paymentAmount": parseFloat(document.getElementById("paymentAmount").value),
        "customerEmail": document.getElementById("customerEmail").value.trim(),
        "eventID": parseInt(document.getElementById("eventID").value),
        "ticketType": document.getElementById("ticketType").value // Ensure ticketType is included
    };

    // Validate form data
    if (!formData.ticketType) {
        alert("Please select a ticket type!");
        return;
    }

    console.log("Sending JSON:", JSON.stringify(formData));

    // Fetch customer ID and call createReservation as before
    var xhr = new XMLHttpRequest();
    xhr.open('GET', `GetCustomerByEmail?email=${encodeURIComponent(formData.customerEmail)}`, true);
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            formData.customerID = response.customer_id; // Add customerID to formData
            createReservation(formData);
        } else {
            alert("Error fetching customer data: " + xhr.responseText);
        }
    };

    xhr.onerror = function () {
        alert("An error occurred while fetching customer data.");
    };

    xhr.send();
}

function createReservation(formData) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'CreateReservation', true);
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    xhr.onload = function () {
        if (xhr.status === 200) {
            alert("Reservation successfully created.");
        } else {
            alert("Error creating reservation: " + xhr.responseText);
        }
    };

    xhr.onerror = function () {
        alert("An error occurred while creating the reservation.");
    };

    xhr.send(JSON.stringify(formData));
}

// Function to format date and time to 'YYYY-MM-DD HH:mm:ss'
function formatDateTime(dateTime) {
    if (!dateTime) return null;
    const [date, time] = dateTime.split("T");
    return `${date} ${time}:00`;
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

function toggleDisplay(elementId) {
    var element = document.getElementById(elementId);
    if (element.style.display === 'none' || element.style.display === '') {
        element.style.display = 'block';
    } else {
        element.style.display = 'none';
    }
}

function getEvents() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse the JSON response
            var events = JSON.parse(xhr.responseText);

            // Create HTML content for each event using createTableEvent function
            var eventListContent = '';
            events.forEach(function (event) {
                eventListContent += createTableEvent(event);
            });

            // Update the #eventList div with the event data
            document.getElementById("eventList").innerHTML = eventListContent;
        } else if (xhr.status !== 200) {
            // Handle errors here, such as displaying a message to the user
            document.getElementById("eventList").innerHTML = "Could not retrieve events data.";
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

document.addEventListener('DOMContentLoaded', function () {
    populateEventDropdown();
});

function populateEventDropdown() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var events = JSON.parse(xhr.responseText);

            // Populate the dropdown
            var eventDropdown = document.getElementById("eventID");
            events.forEach(function (event) {
                var option = document.createElement("option");
                option.value = event.event_id;
                option.textContent = event.name + " (" + event.date + ")";
                eventDropdown.appendChild(option);
            });
        } else if (xhr.status !== 200) {
            console.error("Error retrieving events:", xhr.responseText);
        }
    };

    xhr.open('GET', 'GetEvents');
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send();
}

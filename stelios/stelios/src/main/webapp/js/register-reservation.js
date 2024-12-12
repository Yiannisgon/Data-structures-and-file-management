function submitReservationAndCreate() {
    // Collect form data
    var formData = {
        "ticketCount": parseInt(document.getElementById("ticketCount").value),
        "reservationDate": formatDateTime(document.getElementById("reservationDate").value),
        "paymentAmount": parseFloat(document.getElementById("paymentAmount").value),
        "customerEmail": document.getElementById("customerEmail").value.trim(),
        "eventID": parseInt(document.getElementById("eventID").value)
    };

    // Validate form data
    if (!formData.ticketCount || isNaN(formData.ticketCount) || formData.ticketCount <= 0) {
        alert("Ticket count must be a positive number!");
        return;
    }

    if (!formData.reservationDate) {
        alert("Reservation date is required!");
        return;
    }

    if (!formData.paymentAmount || isNaN(formData.paymentAmount) || formData.paymentAmount <= 0) {
        alert("Payment amount must be a positive number!");
        return;
    }

    if (!formData.customerEmail) {
        alert("Customer email is required!");
        return;
    }

    if (!formData.eventID || isNaN(formData.eventID)) {
        alert("Please select a valid event!");
        return;
    }

    // Fetch the customer ID using the email
    var xhr = new XMLHttpRequest();
    xhr.open('GET', `GetCustomerByEmail?email=${encodeURIComponent(formData.customerEmail)}`, true);
    xhr.onload = function () {
        console.log("GetCustomerByEmail response status:", xhr.status);
        console.log("GetCustomerByEmail response text:", xhr.responseText);

        if (xhr.status === 200) {
            try {
                // Extract customerId directly from the response
                var response = JSON.parse(xhr.responseText);
                var customerId = response.customer_id || response.customerId; // Handle both naming conventions
                console.log("Fetched Customer ID:", customerId);

                if (customerId > 0) {
                    // Add customer ID to the form data
                    formData.customerID = customerId;
                    console.log("Form Data with Customer ID:", formData);

                    // Proceed to create the reservation
                    createReservation(formData);
                } else {
                    alert("No valid customer found with the provided email.");
                }
            } catch (e) {
                console.error("Error parsing customer JSON:", e);
                alert("An error occurred while processing customer data.");
            }
        } else if (xhr.status === 404) {
            alert("Error: Customer not found.");
        } else {
            alert(`Error fetching customer data: ${xhr.responseText}`);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed for GetCustomerByEmail.");
        alert("An error occurred while trying to fetch customer data.");
    };

    xhr.send();
}

function createReservation(formData) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'CreateReservation', true);
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    xhr.onload = function () {
        console.log("CreateReservation response status:", xhr.status);
        console.log("CreateReservation response text:", xhr.responseText);

        if (xhr.status === 200) {
            alert("Reservation successfully created");
            console.log("Response JSON:", xhr.responseText);
        } else {
            alert(`An error occurred while creating the reservation: ${xhr.responseText}`);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed for CreateReservation.");
        alert("An error occurred while trying to create the reservation.");
    };

    console.log("Sending JSON:", JSON.stringify(formData));
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

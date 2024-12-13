function logout() {
    window.location.href = 'index.html'; // Redirect to the login page
}

function initDB() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Successful Initialization");
        } else {
            console.error("Error Occurred");
        }
    };

    xhr.open('GET', 'InitDB');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function deleteDB() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Successful Deletion");
        } else {
            console.error("Error Occurred");
        }
    };

    xhr.open('GET', 'DeleteDB');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function toggleDisplay(elementId) {
    var element = document.getElementById(elementId);
    if (element.style.display === "none" || element.style.display === '') {
        element.style.display = "block";
    } else {
        element.style.display = "none";
    }
}

function closeModal() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => modal.style.display = 'none');
}

// Close the modal if the user clicks outside of it
window.onclick = function (event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
};

function CreateUser() {
    window.location.href = 'user_register.html';
}

function CreateEvent() {
    window.location.href = 'new-event.html';  // Redirect to the new-event page
}

function showAllEvents() {
    getEvents();
    toggleDisplay('eventListModal');
}

function getEvents() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var events = JSON.parse(xhr.responseText);
            var eventListContent = '';
            events.forEach(function (event) {
                eventListContent += createTableEvent(event);
            });
            document.getElementById("eventList").innerHTML = eventListContent;
        } else {
            document.getElementById("eventList").innerHTML = "Could not retrieve events data.";
        }
    };

    xhr.open('GET', 'GetEvents');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function createTableEvent(event) {
    var html = '<table>';
    html += `<tr><th colspan="2">${event.name}</th></tr>`;

    if (event.event_id) {
        html += `<tr><td>Event ID</td><td>${event.event_id}</td></tr>`;
    }

    ['name', 'capacity', 'date', 'time', 'type'].forEach(function (key) {
        if (event[key]) {
            html += `<tr><td>${key.charAt(0).toUpperCase() + key.slice(1)}</td><td>${event[key]}</td></tr>`;
        }
    });

    html += `<tr><td colspan="2"><button onclick="deleteEvent(${event.event_id})">Delete Event</button></td></tr>`;
    html += '</table><br>';
    return html;
}

function deleteEvent(eventId) {
    if (!eventId || isNaN(eventId)) {
        alert("Invalid Event ID!");
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Event deleted: " + eventId);
            getEvents();
        } else {
            console.error("Error deleting event: " + eventId);
        }
    };

    xhr.open('GET', `DeleteEvent?event_id=${eventId}`);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function showAllReservations() {
    getReservations();
    toggleDisplay('reservationListModal');
}

function getReservations() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var reservations = JSON.parse(xhr.responseText);
            var reservationListContent = '';
            reservations.forEach(function (reservation) {
                reservationListContent += createTableReservation(reservation);
            });
            document.getElementById("reservationList").innerHTML = reservationListContent;
        } else {
            document.getElementById("reservationList").innerHTML = "Could not retrieve reservations data.";
        }
    };

    xhr.open('GET', 'GetReservations');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function createTableReservation(reservation) {
    var html = '<table>';
    html += `<tr><th colspan="2">Reservation ID: ${reservation.reservationId}</th></tr>`;

    ['customerID', 'eventID', 'ticketCount', 'paymentAmount', 'reservationDate', 'ticketType'].forEach(function (key) {
        if (reservation[key]) {
            html += `<tr><td>${key.replace(/([A-Z])/g, ' $1').trim()}</td><td>${reservation[key]}</td></tr>`;
        }
    });

    html += `<tr><td colspan="2"><button onclick="deleteReservation(${reservation.reservationId})">Delete Reservation</button></td></tr>`;
    html += '</table><br>';
    return html;
}

function deleteReservation(reservationId) {
    if (!reservationId || isNaN(reservationId)) {
        alert("Invalid Reservation ID!");
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Reservation deleted: " + reservationId);
            getReservations();
        } else {
            console.error("Error deleting reservation: " + reservationId);
        }
    };

    xhr.open('GET', `DeleteReservation?reservation_id=${reservationId}`);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function showAllTickets() {
    toggleDisplay('ticketListModal');
}

function getTotalRevenue() {
    var revenueDisplay = document.getElementById("totalRevenueDisplay");

    if (revenueDisplay.style.display === "none" || revenueDisplay.style.display === "") {
        revenueDisplay.style.display = "block";

        // Fetch revenue data
        var xhr = new XMLHttpRequest();
        xhr.onload = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    try {
                        var revenueData = JSON.parse(xhr.responseText);

                        // Extract refund income
                        var refundIncome = revenueData.refundIncome || 0;

                        // Calculate total income
                        var totalIncome = Object.values(revenueData)
                            .filter(value => typeof value === "number" && !isNaN(value))
                            .reduce((sum, value) => sum + value, 0);

                        // Construct the display content
                        var revenueContent = "<h3>Total Revenue:</h3><ul>";
                        for (var key in revenueData) {
                            if (revenueData.hasOwnProperty(key) && key !== "refundIncome") {
                                revenueContent += `<li>${key}: $${revenueData[key].toFixed(2)}</li>`;
                            }
                        }

                        // Add refund income and total income
                        revenueContent += `<li>Refund income: $${refundIncome.toFixed(2)}</li>`;
                        revenueContent += `<li>Total income: $${totalIncome.toFixed(2)}</li>`;
                        revenueContent += "</ul>";

                        // Update the DOM
                        revenueDisplay.innerHTML = revenueContent;
                    } catch (error) {
                        console.error("Error parsing revenue data:", error);
                        revenueDisplay.innerHTML = "Error parsing revenue data.";
                    }
                } else {
                    console.error("Failed to fetch revenue. HTTP Status:", xhr.status);
                    revenueDisplay.innerHTML = "Error fetching revenue.";
                }
            }
        };

        xhr.onerror = function () {
            console.error("XHR encountered an error.");
            revenueDisplay.innerHTML = "Error fetching revenue.";
        };

        xhr.open("GET", "TotalRevenueServlet", true); // Ensure this URL matches your servlet's mapping
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.send();
    } else {
        revenueDisplay.style.display = "none";
    }
}

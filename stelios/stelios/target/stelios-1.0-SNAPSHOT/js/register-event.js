function submitForm() {
    // Collect form data for the event
    let timeInput = document.getElementById("time").value;

    // Ensure time format includes seconds
    if (!timeInput.includes(":")) {
        alert("Invalid time format. Please use HH:mm.");
        return;
    }
    if (timeInput.split(":").length === 2) {
        timeInput += ":00"; // Append seconds if missing
    }

    var eventData = {
        "name": document.getElementById("name").value.trim(),
        "date": document.getElementById("date").value.trim(),
        "time": timeInput.trim(),
        "capacity": parseInt(document.getElementById("capacity").value),
        "type": document.getElementById("type").value.trim()
    };

    // Collect ticket data from the form
    var vipTicketData = {
        "price": parseFloat(document.getElementById("vip-price").value),
        "availability": parseInt(document.getElementById("vip-availability").value),
        "type": "VIP"
    };

    var gaTicketData = {
        "price": parseFloat(document.getElementById("ga-price").value),
        "availability": parseInt(document.getElementById("ga-availability").value),
        "type": "GeneralAdmission"
    };

    // Validate event form data
    if (!eventData.name || !eventData.date || !eventData.time || !eventData.capacity || !eventData.type) {
        alert("All event fields are required!");
        return;
    }

    if (isNaN(eventData.capacity) || eventData.capacity <= 0) {
        alert("Capacity must be a positive number!");
        return;
    }

    // Validate ticket form data
    if (isNaN(vipTicketData.price) || vipTicketData.price <= 0 || isNaN(vipTicketData.availability) || vipTicketData.availability <= 0) {
        alert("VIP ticket price and availability must be positive numbers!");
        return;
    }

    if (isNaN(gaTicketData.price) || gaTicketData.price <= 0 || isNaN(gaTicketData.availability) || gaTicketData.availability <= 0) {
        alert("General Admission ticket price and availability must be positive numbers!");
        return;
    }

    // Initialize XMLHttpRequest for event creation
    var xhrEvent = new XMLHttpRequest();
    xhrEvent.open('POST', 'CreateEvent', true); // Endpoint for event creation
    xhrEvent.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    // Define what happens on successful event creation
    xhrEvent.onload = function() {
        if (xhrEvent.status === 200) {
            console.log("Event successfully registered");
            var eventResponse = JSON.parse(xhrEvent.responseText);
            // Now that the event is created, create tickets for the event
            createTicket(eventResponse.event_id, vipTicketData, gaTicketData);
        } else {
            alert("An error occurred while creating the event: " + xhrEvent.responseText);
            console.error("Error response:", xhrEvent.responseText);
        }
    };

    // Send collected event data as JSON
    console.log("Sending Event JSON:", JSON.stringify(eventData));
    xhrEvent.send(JSON.stringify(eventData));
}

function createTicket(eventId, vipTicketData, gaTicketData) {
    // Add event ID to the ticket data
    vipTicketData.event_id = eventId;
    gaTicketData.event_id = eventId;

    // Create VIP ticket
    var xhrVIP = new XMLHttpRequest();
    xhrVIP.open('POST', 'CreateTicket', true); // Endpoint for ticket creation
    xhrVIP.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    // Handle VIP ticket creation response
    xhrVIP.onload = function() {
        if (xhrVIP.status === 200) {
            console.log("VIP ticket successfully created");
        } else {
            alert("An error occurred while creating the VIP ticket: " + xhrVIP.responseText);
            console.error("Error response:", xhrVIP.responseText);
        }
    };

    // Send VIP ticket data
    console.log("Sending VIP Ticket JSON:", JSON.stringify(vipTicketData));
    xhrVIP.send(JSON.stringify(vipTicketData));

    // Create General Admission ticket
    var xhrGA = new XMLHttpRequest();
    xhrGA.open('POST', 'CreateTicket', true); // Endpoint for ticket creation
    xhrGA.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    // Handle General Admission ticket creation response
    xhrGA.onload = function() {
        if (xhrGA.status === 200) {
            console.log("General Admission ticket successfully created");
        } else {
            alert("An error occurred while creating the General Admission ticket: " + xhrGA.responseText);
            console.error("Error response:", xhrGA.responseText);
        }
    };

    // Send General Admission ticket data
    console.log("Sending General Admission Ticket JSON:", JSON.stringify(gaTicketData));
    xhrGA.send(JSON.stringify(gaTicketData));
}

function submitForm() {
    // Collect form data
    let timeInput = document.getElementById("time").value;

    // Ensure time format includes seconds
    if (!timeInput.includes(":")) {
        alert("Invalid time format. Please use HH:mm.");
        return;
    }
    if (timeInput.split(":").length === 2) {
        timeInput += ":00"; // Append seconds if missing
    }

    var formData = {
        "name": document.getElementById("name").value.trim(),
        "date": document.getElementById("date").value.trim(),
        "time": timeInput.trim(),
        "capacity": parseInt(document.getElementById("capacity").value),
        "type": document.getElementById("type").value.trim()
    };

    // Validate form data
    if (!formData.name || !formData.date || !formData.time || !formData.capacity || !formData.type) {
        alert("All fields are required!");
        return;
    }

    if (isNaN(formData.capacity) || formData.capacity <= 0) {
        alert("Capacity must be a positive number!");
        return;
    }

    // Log the form data for debugging
    console.log("Form Data:", formData);

    // Initialize XMLHttpRequest for form submission
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'CreateEvent', true); // Adjusted endpoint for event registration
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    // Define what happens on successful data submission
    xhr.onload = function() {
        if (xhr.status === 200) {
            alert("Event successfully registered");
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
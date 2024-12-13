

// Simplified JavaScript for the user registration form
// Simplified JavaScript for the user registration form
function submitForm() {
    // Collect form data
    var formData = {
        "name": document.getElementById("name").value,
        "email": document.getElementById("email").value,
        "credit_card_details": document.getElementById("credit_card").value,
        "balance": parseFloat(document.getElementById("balance").value) || 0 // Default to 0 if not specified
    };

    // Log the form data
    console.log("Form Data:", formData);

    // Initialize XMLHttpRequest for form submission
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'CreateCustomer', true);
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

    // Define what happens on successful data submission
    xhr.onload = function() {
        if (xhr.status === 200) {
            alert("User successfully registered");
        } else {
            alert("An error occurred: " + xhr.responseText);
        }
    };

    // Send collected data as JSON
    xhr.send(JSON.stringify(formData));
}



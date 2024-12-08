document.addEventListener('DOMContentLoaded', function() {
    // Ensure the DOM is fully loaded before attaching the event listener
    document.getElementById('credentials-form').addEventListener('submit', editInfo);
});

function editInfo(event) {
    event.preventDefault(); // Prevents the default form submission

    var username = sessionStorage.getItem('username'); // Get username from session storage
    if (!username) {
        alert("User not logged in.");
        return;
    }

    // Collecting form data
    var formData = {
        username: username,
        password: document.getElementById('password').value,
        firstname: document.getElementById('firstname').value,
        lastname: document.getElementById('lastname').value,
        birthdate: document.getElementById('birthdate').value,
        gender: document.getElementById('gender').value,
        country: document.getElementById('country').value,
        city: document.getElementById('city').value,
        address: document.getElementById('address').value,
        personalpage: document.getElementById('personalpage').value,
        job: document.getElementById('job').value,
        telephone: document.getElementById('telephone').value,
        property: document.getElementById('property').value,
        propertydescription: document.getElementById('propertydescription').value,
        catprice: document.getElementById('catprice').value,
        dogprice: document.getElementById('dogprice').value
    };

    // print the form data on the console
    console.log(formData);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'EditInfoKeeper', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onload = function() {
        if (this.status === 200) {
            alert('Information updated successfully!');
        } else {
            alert('Failed to update information. Please try again.');
        }
    };

    xhr.onerror = function() {
        alert('Network error. Please try again.');
    };

    xhr.send(JSON.stringify(formData)); // Send the collected data as JSON
}

function fetchAndDisplayStatistics() {
    console.log("fetchAndDisplayStatistics");

    var username = sessionStorage.getItem('username');
    if (!username) {
        alert("User not logged in.");
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'Stats', true);

    xhr.onload = function() {
        if (this.status === 200) {
            try {
                var data = JSON.parse(this.responseText);
                processAndDisplay(data, username);
            } catch (e) {
                console.error('Error parsing JSON:', e);
                alert('Failed to parse statistics. Please try again.');
            }
        } else {
            console.error('Error fetching statistics:', this.status);
            alert('Failed to load statistics. Please try again.');
        }
    };

    xhr.onerror = function() {
        console.error('Network error.');
        alert('Network error. Please try again.');
    };

    xhr.send();
}

function processAndDisplay(data, username) {
    // Find the keeper ID based on the username
    let keeper = data.keepers.find(k => k.username === username);
    if (!keeper) {
        console.error('Keeper not found');
        alert('Keeper not found. Please try again.');
        return;
    }

    let keeperId = keeper.id; // assuming the keeper object has an 'id' field

    // Filter and process the bookings based on the keeper's ID
    let keeperBookings = data.bookings.filter(booking => booking.keeperId === keeperId && booking.status === 'finished');

    // Calculate statistics
    let numberOfBookingsFinished = keeperBookings.length;
    let totalDays = keeperBookings.reduce((sum, booking) => {
        let fromDate = new Date(booking.fromDate);
        let toDate = new Date(booking.toDate);
        let duration = (toDate - fromDate) / (1000 * 60 * 60 * 24); // Convert milliseconds to days
        return sum + duration;
    }, 0);

    let totalEarnings = keeperBookings.reduce((sum, booking) => sum + booking.price, 0); // assuming each booking has a 'price' field

    // Display statistics
    displayStatistics(numberOfBookingsFinished, totalDays, totalEarnings);
}

function displayStatistics(numberOfBookingsFinished, totalDays, totalEarnings) {
    document.getElementById('bookings-finished').textContent = numberOfBookingsFinished;
    document.getElementById('total-days').textContent = totalDays;
    document.getElementById('total-earnings').textContent = totalEarnings.toFixed(2);
    // Reviews handling can be added here
}
function getChatCompletion() {
    // Get user input
    var userInput = document.getElementById("userInput").value;
    var key="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"; // PUT YOUR KEY

    // Prepare the request payload
    var payload = {
        "model": "gpt-3.5-turbo",
        "messages": [
            {"role": "user", "content": userInput}
        ],
        "temperature": 1.0,
        "max_tokens": 1000
    };

    // Convert payload to string
    var payloadString = JSON.stringify(payload)
        .replace(/"\[/g, '[')
        .replace(/\]"/g, ']')
        .replace(/'/g, '"')
        .replace(/"MyText"/g, '"' + userInput + '"');

    // Make HTTP request to OpenAI API
    var xhr = new XMLHttpRequest();
    var url = "https://api.openai.com/v1/chat/completions";

    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", "Bearer "+ key);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse and display the response
            var response = JSON.parse(xhr.responseText);
            document.getElementById("response").value = response.choices[0].message.content;
        } else {
            // Handle errors or other status codes here
            console.error("Error: " + xhr.status);
        }
    };

    // Send the request
    xhr.send(payloadString);
}
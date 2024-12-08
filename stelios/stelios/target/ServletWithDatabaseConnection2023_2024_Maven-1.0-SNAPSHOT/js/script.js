function continueAsGuest() {
    /* go to guest's page */

    window.location.href = 'guest.html';
    alert('Welcome, guest! You can explore our pet services.');
    // Add any additional logic for continuing as a guest here
}

document.addEventListener('DOMContentLoaded', function () {
    // Function to fetch JSON data and display petkeepers
    function displayPetkeepers() {
        // Fetch JSON data (replace 'petkeeper1.json' with the actual filename)
        fetch('petkeeper1.json')
            .then(response => response.json())
            .then(data => {
                // Append the petkeeper's name to the list
                const petkeeperList = document.getElementById('petkeeperList');
                const listItem = document.createElement('li');
                listItem.textContent = data.name; // Assuming 'name' is a property in your JSON
                petkeeperList.appendChild(listItem);
            })
            .catch(error => console.error('Error fetching JSON:', error));
    }

    // Call the function to display petkeepers
    displayPetkeepers();
});

function checkCredentials(event) {
    event.preventDefault(); // Prevents the form from submitting normally

    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    // Check for admin credentials
    if (username === 'admin' && password === 'admin12*') {
        // Redirect to the admin page
        window.location.href = 'admin.html';
        return;
    }

    // Function to handle pet keeper login
    function checkPetKeeper() {
        var xhrKeeper = new XMLHttpRequest();
        xhrKeeper.open('GET', 'GetKeeper?username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password), true);

        xhrKeeper.onload = function() {
            if (this.status == 200) {
                // Login successful for pet keeper
                alert('Login successful!');
                window.location.href = 'petkeeper.html';
            } else {
                // If not a pet keeper, check if user is a pet owner
                checkPetOwner();
            }
        };

        xhrKeeper.onerror = function() {
            // Handle network errors
            alert('Network error. Please try again.');
        };

        xhrKeeper.send();
    }

    // Function to handle pet owner login
    function checkPetOwner() {
        var xhrOwner = new XMLHttpRequest();
        xhrOwner.open('GET', 'GetOwner?username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password), true);

        xhrOwner.onload = function() {
            if (this.status == 200) {
                // Login successful for pet owner
                alert('Login successful as Pet Owner!');
                window.location.href = 'petowner.html';
            } else {
                // Login failed
                alert('Login failed. Please check your username and password. You will now be redirected to the registration page.');
                window.location.href = 'user_register.html';
            }
        };

        xhrOwner.onerror = function() {
            // Handle network errors
            alert('Network error. Please try again.');
        };

        xhrOwner.send();
    }

    // First, check if the user is a pet keeper
    checkPetKeeper();
}
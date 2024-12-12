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
    if (username === 'admin' && password === '') {
        // Redirect to the admin page
        window.location.href = 'admin.html';
        return;
    }

    function saveUserInfo(username) {
        var xhrSaveInfo = new XMLHttpRequest();
        xhrSaveInfo.open('GET', 'Users?username=' + encodeURIComponent(username), true);
        //print the user info
        console.log('Users?username=' + encodeURIComponent(username));

        xhrSaveInfo.onload = function() {
            if (this.status === 200) {

            } else {
                alert('Failed to save user information.');
            }
        };

        xhrSaveInfo.onerror = function() {
            alert('Network error while saving user information. Please try again.');
        };

        xhrSaveInfo.send();
    }

    function loginSuccess(url) {
        saveUserInfo(username);

        // Save username in session storage
        sessionStorage.setItem('username', username);

        alert('Login successful!');
        window.location.href = url;
    }
    function checkPetKeeper() {
        var xhrKeeper = new XMLHttpRequest();
        xhrKeeper.open('GET', 'GetKeeper?username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password), true);

        xhrKeeper.onload = function() {
            if (this.status === 200) {
                loginSuccess('petkeeper.html');
            } else {
                checkPetOwner();
            }
        };

        xhrKeeper.onerror = function() {
            alert('Network error. Please try again.');
        };

        xhrKeeper.send();
    }

    function checkPetOwner() {
        var xhrOwner = new XMLHttpRequest();
        xhrOwner.open('GET', 'GetOwner?username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password), true);

        xhrOwner.onload = function() {
            if (this.status === 200) {
                loginSuccess('petowner.html');
            } else {
                alert('Login failed. Please check your username and password.');
            }
        };

        xhrOwner.onerror = function() {
            alert('Network error. Please try again.');
        };

        xhrOwner.send();
    }

    checkPetKeeper();
}


function redirectToRegister() {
    // Redirect to the registration page
    window.location.href = 'user_register.html'; // Change 'register.html' to your actual registration page URL
}

function editInfo() {

}
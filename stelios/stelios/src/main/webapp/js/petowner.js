// Modified showAllUsers function
function showAllUsers() {
    // Call getKeepers and getOwners to retrieve and display data
    getKeepers();
    // Toggle the modal display
    toggleDisplay('userListModal');
}
// Function to get petKeepers data and update the #keepersList div
function getKeepers() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse the JSON response
            var keepers = JSON.parse(xhr.responseText);

            // Create HTML content for each keeper using createTableKeeper function
            var keepersListContent = '';
            keepers.forEach(function(keeper) {
                keepersListContent += createTableUser(keeper);
            });

            // Update the #keepersList div with the keeper data
            $("#keeperList").html(keepersListContent);
        } else if (xhr.status !== 200) {
            // Handle errors here, such as displaying a message to the user
            $("#keeperList").html("Could not retrieve keepers data.");
        }
    };

    xhr.open('GET', 'Users?type=keeper');
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
    // Close the modal
    const modal = document.getElementById('userListModal');
    modal.style.display = 'none';
}

function createTableUser(user) {
    var html = '<table>';
    html += '<tr><th colspan="2">' + user.firstname + ' ' + user.lastname + '</th></tr>';

    // Determine the ID and type field to use (keeper_id/owner_id and type)
    var idField = user.keeper_id || user.owner_id;
    var userType = user.keeper_id ? 'keeper' : 'owner';
    var username;
    if (idField) {
        html += '<tr><td>user id</td><td>' + idField + '</td></tr>';
    }

    var includeKeys = ['firstname', 'lastname', 'username'];
    includeKeys.forEach(function(key) {
        if (user.hasOwnProperty(key) && user[key] !== null) {
            var value = user[key];
            if(key==="username") {
                username  = value;
            }
            html += '<tr><td>' + key + '</td><td>' + value + '</td></tr>';
        }
    });
    return html;
}

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
    };

    // print the form data on the console
    console.log(formData);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'EditInfoOwner', true);
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
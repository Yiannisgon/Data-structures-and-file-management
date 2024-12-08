function logout() {
    window.location.href = 'index.html'; // Redirect to the login page
}
function initDB() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Successful Initialization");
        } else if (xhr.status !== 200) {
            console.log("Error Occured");
        }
    };

    xhr.open('GET', 'InitDB');
    xhr.setRequestHeader('Content-type','application/x-www-form-urlencoded');
    xhr.send();
}

function deleteDB() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Successful Deletion");
        } else if (xhr.status !== 200) {
            console.log("Error Occured");
        }
    };

    xhr.open('GET', 'DeleteDB');
    xhr.setRequestHeader('Content-type','application/x-www-form-urlencoded');
    xhr.send();
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

// Function to get owners data and update the #ownersList div
function getOwners() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse the JSON response
            var owners = JSON.parse(xhr.responseText);

            // Create HTML content for each owner using createTableOwner function
            var ownersListContent = '';
            owners.forEach(function(owner) {
                ownersListContent += createTableUser(owner);
            });

            // Update the #ownersList div with the owner data
            $("#ownerList").html("");
            $("#ownerList").html(ownersListContent);
        } else if (xhr.status !== 200) {
            // Handle errors here, such as displaying a message to the user
            $("#ownerList").html("Could not retrieve owners data.");
        }
    };

    xhr.open('GET', 'Users?type=owner'); // Replace 'GetOwners' with your actual endpoint
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

// Function to show all users and call getKeepers and getOwners
function showAllUsers() {
    // Call getKeepers and getOwners to retrieve and display data
    getKeepers();
    getOwners();
    // Display the modal
    $('#userListModal').css('display', 'block');

}

//function showStatistics() {
    // Replace the following with your logic to fetch and display statistics
//    alert('Statistics: Total Users - 100, Active Users - 80, Inactive Users - 20');
//}

function closeModal() {
    // Close the modal
    const modal = document.getElementById('userListModal');
    modal.style.display = 'none';
}

// Close the modal if the user clicks outside of it
window.onclick = function(event) {
    const modal = document.getElementById('userListModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
};

function createTableUser(user) {
    var html = '<table>';
    html += '<tr><th colspan="2">' + user.firstname + ' ' + user.lastname + '</th></tr>';

    // Determine the ID field to use (keeper_id or owner_id)
    var idField = user.keeper_id || user.owner_id;

    if (idField) {
        // Include the ID field if it exists
        html += '<tr><td>id</td><td>' + idField + '</td></tr>';
    }

    // Define an array of keys that you want to include
    var includeKeys = ['firstname', 'lastname', 'username'];

    includeKeys.forEach(function(key) {
        if (user.hasOwnProperty(key) && user[key] !== null) {
            var value = user[key];
            html += '<tr><td>' + key + '</td><td>' + value + '</td></tr>';
        }
    });

    // Add delete user button
    html += '<tr><td colspan="2"><button onclick="deleteUser(\'' + user.username + '\')">Delete User</button></td></tr>';
    html += '</table><br>';
    return html;
}


function deleteUser(username) {
    // Send a GET request to the server to delete the user with the specified username
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // User deleted successfully
            console.log("User deleted: " + username);
        } else if (xhr.status !== 200) {
            // Handle errors here, such as displaying a message to the user
            console.error("Error deleting user: " + username);
        }
    };

    xhr.open('GET', 'DeleteUser?username=' + username);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}


// Load Google Charts
google.charts.load('current', {'packages':['corechart']});
// Assume global data object that will hold the fetched data
var globalStatisticsData = {};

function showStatistics() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse the entire JSON response
            globalStatisticsData = JSON.parse(xhr.responseText);
            // Now draw each chart
            drawCatDogChart();
            drawOwnerKeeperChart();
            drawEarningsChart();
        } else if (xhr.status !== 200) {
            console.error("Error fetching statistics data");
        }
    };
    xhr.open('GET', 'Stats');
    xhr.send();
}

function drawCatDogChart() {
    // Process pets data to count cats and dogs
    var pets = globalStatisticsData.pets;
    var catNum = pets.filter(pet => pet.type === 'cat').length;
    var dogNum = pets.filter(pet => pet.type === 'dog').length;

    // Create the data table
    var data = google.visualization.arrayToDataTable([
        ['Pet Type', 'Number'],
        ['Cats', catNum],
        ['Dogs', dogNum]
    ]);

    // Chart options
    var options = {
        title: 'Number of Cats and Dogs',
        // ... other options ...
    };

    // Draw the chart
    var chart = new google.visualization.PieChart(document.getElementById('catDogChart'));
    chart.draw(data, options);
}

function drawOwnerKeeperChart() {
    // Process owners and keepers data
    var ownersNum = globalStatisticsData.owners.length;
    var keepersNum = globalStatisticsData.keepers.length;

    // Create the data table
    var data = google.visualization.arrayToDataTable([
        ['User Type', 'Number'],
        ['Pet Owners', ownersNum],
        ['Pet Keepers', keepersNum]
    ]);

    // Chart options
    var options = {
        title: 'Number of Pet Owners and Pet Keepers',
        // ... other options ...
    };

    // Draw the chart
    var chart = new google.visualization.PieChart(document.getElementById('ownerKeeperChart'));
    chart.draw(data, options);
}

function drawEarningsChart() {
    // We assume globalStatisticsData is already defined and populated
    var {keepersEarnings, appEarnings} = calculateEarnings(globalStatisticsData.bookings);

    // Create the data table
    var data = google.visualization.arrayToDataTable([
        ['Source', 'Earnings'],
        ['Keepers', keepersEarnings],
        ['App', appEarnings]
    ]);

    // Chart options
    var options = {
        title: 'Money Earned by Each Keeper and by the App',
        // ... other options ...
    };

    var chart = new google.visualization.PieChart(document.getElementById('earningsChart'));
    chart.draw(data, options);
}

function calculateEarnings(bookings) {
    let totalEarnings = 0;
    if (!bookings) {
        console.error('bookings is undefined');
        return {keepersEarnings: 0, appEarnings: 0};
    }
    // Calculate total earnings from finished bookings
    bookings.forEach(booking => {
        if(booking.status === 'finished') {
            totalEarnings += booking.price;
        }
    });

    // Calculate earnings for keepers and app
    const keepersEarnings = totalEarnings * 0.85;
    const appEarnings = totalEarnings * 0.15;

    return {keepersEarnings, appEarnings};
}

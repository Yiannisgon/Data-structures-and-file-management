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

// Modified showAllUsers function
function showAllUsers() {
    // Call getKeepers and getOwners to retrieve and display data
    getKeepers();
    getOwners();
    // Toggle the modal display
    toggleDisplay('userListModal');
}


// Modified showStatistics function
function showStatistics() {
    // Fetch and prepare data if not already fetched
    if (Object.keys(globalStatisticsData).length === 0) {
        fetchStatisticsData();
    } else {
        // Toggle display for each chart
        toggleDisplay('catDogChart');
        toggleDisplay('ownerKeeperChart');
        toggleDisplay('earningsChart');
    }
}
// New function to fetch statistics data
function fetchStatisticsData() {
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

    // Add delete user button with ID and type
    //html += '<tr><td colspan="2"><button onclick="deleteUser(\'' + username + '\')">Delete User</button></td></tr>';
    html += '<tr><td colspan="2"><button onclick="deleteUser(\'' + idField + '\', \'' + userType + '\')">Delete User</button></td></tr>';
    html += '</table><br>';
    return html;
}



function deleteUser(userId, userType) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("User deleted: " + userId);
            // After successful deletion, refresh the keeper and owner lists
            getKeepers();
            getOwners();
        } else if (xhr.status !== 200) {
            console.error("Error deleting user: " + userId);
        }
    };

    // Adjust the URL to include user ID and type
    var url = 'DeleteUser?id=' + userId + '&type=' + userType;
    xhr.open('GET', url);
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

function CreateUser() {
    return;
}
function CreateEvent() {
    return;
}
function getAvailableTickets() {
    return;
}
function getEvents() {
    return;
}

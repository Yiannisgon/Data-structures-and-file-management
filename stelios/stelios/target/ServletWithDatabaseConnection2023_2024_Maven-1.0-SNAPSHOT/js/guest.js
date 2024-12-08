
function createTableForPetKeeper(petKeeper) {
    var html = '<table>'; // Removed inline style
    html += '<tr><th colspan="2">' + petKeeper.firstname + ' ' + petKeeper.lastname + '</th></tr>';

    // Define an array of keys that we want to exclude from displaying
    var excludeKeys = ['password', 'keeper_id', 'username'];

    for (var key in petKeeper) {
        // Check if the key is not one of the excluded ones and the value is not null
        if (petKeeper.hasOwnProperty(key) && excludeKeys.indexOf(key) === -1 && petKeeper[key] !== null) {
            var value = petKeeper[key];
            // Format the value if it's a date or similar
            if (key === 'birthdate') {
                value = new Date(petKeeper[key]).toLocaleDateString(); // Assuming the birthdate is in a format that Date can parse
            }
            html += '<tr><td>' + key + '</td><td>' + value + '</td></tr>';
        }
    }
    html += '</table><br>';
    return html;
}


function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function displayPetKeepers(responseText) {
    var petKeepers = JSON.parse(responseText);
    var html = '';
    petKeepers.forEach(function(petKeeper) {
        html += createTableForPetKeeper(petKeeper);
    });
    $("#availableKeepers").html(html);
}

function getAvailableKeepers() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            displayPetKeepers(xhr.responseText);
        } else if (xhr.status !== 200) {
            $("#availableKeepers").html("Could not retrieve pet keepers data.");
        }
    };

    xhr.open('GET', 'AvailableKeepers');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}
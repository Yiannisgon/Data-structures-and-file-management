//create variables latitude longtitude and display name and dont initialise them as 0
var lat = 0;
var lon = 0;
var display_name = 0;


 //Function to make the password visible
    function togglePasswordVisibility() {
        var passwordInput = document.getElementById("password");
        if (passwordInput.type === "password") {
            passwordInput.type = "text";
        } else {
            passwordInput.type = "password";
        }
    }

// Function to check password strength and enable/disable the Sign Up button
function checkPasswordMatch() {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirm_password").value;
    var message = document.getElementById("passwordMatchMessage");
    var signUpButton = document.getElementById("signup-button"); // Add an ID to your "Sign Up" button

    if (password === confirmPassword) {
        var numericCount = countNumericCharacters(password);
        var passwordLength = password.length;

        if (numericCount / passwordLength > 0.5) {
            message.textContent = "Weak password (50% or more digits)";
            message.style.color = "red";
            signUpButton.disabled = true; // Disable the "Sign Up" button
        } else if (isStrongPassword(password)) {
            message.textContent = "Strong password";
            message.style.color = "green";
            signUpButton.disabled = false; // Enable the "Sign Up" button
        } else if (hasProhibitedSequences(password)) {
            message.textContent = "Password contains prohibited sequences";
            message.style.color = "red";
            signUpButton.disabled = true; // Disable the "Sign Up" button
        } else {
            message.textContent = "Passwords match and are of medium safety";
            message.style.color = "yellow";
            signUpButton.disabled = false; // Enable the "Sign Up" button
        }
    } else {
        message.textContent = "Passwords do not match";
        message.style.color = "red";
        signUpButton.disabled = true; // Disable the "Sign Up" button
    }
}

    
    function isStrongPassword(str) {
        // Define regular expressions for character types
        var symbolRegex = /[\W_]/;
        var uppercaseRegex = /[A-Z]/;
        var lowercaseRegex = /[a-z]/;
        var digitRegex = /\d/;
    
        // Check if the password contains at least one of each character type
        return (
            symbolRegex.test(str) &&
            uppercaseRegex.test(str) &&
            lowercaseRegex.test(str) &&
            digitRegex.test(str)
        );
    }
    
    function countNumericCharacters(str) {
        var numericCount = 0;
        for (var i = 0; i < str.length; i++) {
            if (!isNaN(parseInt(str[i]))) {
                numericCount++;
            }
        }
        return numericCount;
    }
    
    
    function hasProhibitedSequences(password) {
        // Define the prohibited sequences
        var prohibitedSequences = ["cat", "dog", "gata", "skulos"];
    
        // Check if any prohibited sequence exists in the password
        for (var i = 0; i < prohibitedSequences.length; i++) {
            if (password.includes(prohibitedSequences[i])) {
                return true; // Password contains a prohibited sequence
            }
        }
    
        return false; // Password is safe
    }
    

    function showAdditionalFields() {
        var userTypeSelect = document.getElementById("type");
        var petKeeperFields = document.getElementById("pet-keeper-fields");
        
        if (userTypeSelect.value === "pet-keeper") {
            petKeeperFields.style.display = "block";
        } else {
            petKeeperFields.style.display = "none";
        }
    }

    function loadDoc() {
        const xhr = new XMLHttpRequest();
        xhr.withCredentials = true;

        xhr.addEventListener("readystatechange", function () {
        if (this.readyState === this.DONE) {
            if (this.status === 200) {
                // Parse the response as JSON
                
                    const response = JSON.parse(xhr.responseText);
                    //type in log the response lat and lon and display_name
                    console.log(response[0].lat, response[0].lon, response[0].display_name);
                    //create map
                    createMap(response[0].lat, response[0].lon, response[0].display_name);
                    
                    if (Array.isArray(response)) {
                        
                        const isHeraklion = response.some(item => item.display_name.includes("Heraklion"));

                        if (isHeraklion) {
                            document.getElementById("demo").innerHTML = "Location is valid and available in Heraklion";
                        } else {
                            document.getElementById("demo").innerHTML = "Sorry, but this service is only available in Heraklion";
                        }
                    } else {
                        // Handle the case where the response is not an array
                        console.error("Error: Invalid response format");
                        document.getElementById("demo").innerHTML = "An error occurred(Invalid location). Please try again.";
                    }
                
            } else {
                // Handle errors, for example:
                console.error("Error: Unable to fetch data. Status code:", this.status);
                document.getElementById("demo").innerHTML = "An error occurred(Invalid location). Please try again.";
                }
            }
        });
        
        
        // Get values from user input
        var addressName = document.getElementById("address").value;
        var city = document.getElementById("city").value;
        var country = document.getElementById("country").value;
        var address = addressName + " " + city + " " + country;
    
        // Construct the URL for the geocoding service
        xhr.open("GET", "https://forward-reverse-geocoding.p.rapidapi.com/v1/search?q=" + address + "&accept-language=en&polygon_threshold=0.0");
    
        xhr.setRequestHeader("x-rapidapi-host", "forward-reverse-geocoding.p.rapidapi.com");
        var key = "ffa1d7dd32mshed7ce1b91475ce1p14def5jsnd21b01493cfc";
        xhr.setRequestHeader("x-rapidapi-key", key);
    
        xhr.send();
    }

function submitForm() {

    // Define userType
    var userType = document.getElementById("type").value;

    if (userType === "pet-keeper") {
        //print
        console.log("submitForm() called");
        //get selected accomodation'
    let selectedAccommodation = document.querySelector('input[name="accommodation"]:checked').value;

    var genderElements = document.getElementsByName('gender');
    var selectedGenderRadio = Array.from(genderElements).find(radio => radio.checked);
    var selectedGender = selectedGenderRadio ? selectedGenderRadio.value : null;
    console.log(userType);
    // Collect form data
    var formData = {
        "username": document.getElementById("username").value,
        "email": document.getElementById("email").value,
        "password": document.getElementById("password").value,
        "firstname": document.getElementById("name").value,
        "lastname": document.getElementById("surname").value,
        "birthdate": document.getElementById("birthdate").value,
        "gender": selectedGender,
        "country": document.getElementById("country").value,
        "city": document.getElementById("city").value,
        "address": document.getElementById("address").value,
        "personalpage": document.getElementById("personalpage").value,
        "job": document.getElementById("job").value,
        "telephone": document.getElementById("telephone").value,
        //send lat and lon as test values
        "lat": 35.3387,
        "lon": 25.1442,
        "property": selectedAccommodation,
        "propertydescription": document.getElementById("propertyDescription").value,
        "catkeeper": document.getElementById("catkeeper").checked,
        "dogkeeper": document.getElementById("dogkeeper").checked,
        "catprice": document.getElementById("catprice").value,
        "dogprice": document.getElementById("dogprice").value,
    };
    } else {
        var formData = {
            "username": document.getElementById("username").value,
            "email": document.getElementById("email").value,
            "password": document.getElementById("password").value,
            "firstname": document.getElementById("name").value,
            "lastname": document.getElementById("surname").value,
            "birthdate": document.getElementById("birthdate").value,
            "gender": selectedGender,
            "country": document.getElementById("country").value,
            "city": document.getElementById("city").value,
            "address": document.getElementById("address").value,
            "personalpage": document.getElementById("personalpage").value,
            "job": document.getElementById("job").value,
            "telephone": document.getElementById("telephone").value,
            //send lat and lon as test values
            "lat": 35.3387,
            "lon": 25.1442,
        }
    }

    //print the form data
    console.log(formData);

    // Determine the servlet URL based on the user type
    var servletURL = userType === "pet-keeper" ? "CreatePetKeeper" : "CreatePetOwner";
    //print servletURL
    console.log(servletURL);

    // Initialize XMLHttpRequest
    var xhr = new XMLHttpRequest();
    xhr.open("POST", servletURL, true);
    xhr.setRequestHeader("Content-Type", "application/json");

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


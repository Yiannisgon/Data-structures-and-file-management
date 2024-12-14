

function checkCredentials(event) {
    event.preventDefault(); // Prevents the form from submitting normally

    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    // Check for admin credentials
    if (username === 'admin' && password === 'admin') {
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

}


function redirectToRegister() {
    // Redirect to the registration page
    window.location.href = 'user_register.html'; // Change 'register.html' to your actual registration page URL
}

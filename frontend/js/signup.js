const signupForm = document.getElementById("signupForm");
const signupButton = document.getElementById("signupButton");
const signupMessage = document.getElementById("signupMessage");

const API_BASE_URL = "http://localhost:8081";

signupForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const firstname =
        document.getElementById("firstName").value.trim();

    const lastname =
        document.getElementById("lastName").value.trim();

    const email =
        document.getElementById("email").value.trim();

    const phoneNumber =
        document.getElementById("phone").value.trim();

    const city =
        document.getElementById("city").value.trim();

    const password =
        document.getElementById("password").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;


    // Check password confirmation
    if (password !== confirmPassword) {
        signupMessage.textContent = "Passwords do not match.";
        return;
    }


    // Basic phone validation
    const phoneRegex = /^09[0-9]{9}$/;

    if (!phoneRegex.test(phoneNumber)) {
        signupMessage.textContent =
            "Phone number must start with 09 and contain 11 digits.";

        return;
    }


    signupButton.disabled = true;
    signupButton.textContent = "Creating account...";
    signupMessage.textContent = "";


    try {

        const response = await fetch(
            `${API_BASE_URL}/api/auth/signup`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    firstname: firstname,
                    lastname: lastname,
                    email: email,
                    phoneNumber: phoneNumber,
                    password: password,
                    city: city
                })
            }
        );


        const data = await response.json();


        if (!response.ok) {
            throw new Error(
                data.message || "Registration failed."
            );
        }


        console.log("Registered user:", data);


        signupMessage.textContent =
            "Account created successfully!";


        // Go to login after successful registration
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1000);


    } catch (error) {

        console.error("Signup error:", error);

        signupMessage.textContent =
            error.message ||
            "Something went wrong. Please try again.";


    } finally {

        signupButton.disabled = false;
        signupButton.textContent = "Create Account";
    }

});
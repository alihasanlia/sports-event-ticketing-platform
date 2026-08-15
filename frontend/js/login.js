const loginForm = document.getElementById("loginForm");
const loginButton = document.getElementById("loginButton");
const loginMessage = document.getElementById("loginMessage");

const API_BASE_URL = "http://localhost:8081";

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;

    if (!email || !password || !role) {
        loginMessage.textContent = "Please fill in all fields.";
        return;
    }

    loginButton.disabled = true;
    loginButton.textContent = "Logging in...";
    loginMessage.textContent = "";

    try {

        const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                email: email,
                password: password,
                role: role
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Login failed.");
        }

        // Backend returns: { "token": "..." }
        localStorage.setItem("token", data.token);

        // Save the selected role too
        localStorage.setItem("role", role);
        localStorage.setItem("email", email);

        // Redirect based on role
        if (role === "USER") {
            window.location.href = "index.html";
        }
        else if (role === "SUPPORT") {
            window.location.href = "admin-index.html";
        }

    } catch (error) {

        console.error("Login error:", error);

        loginMessage.textContent =
            error.message || "Something went wrong. Please try again.";

    } finally {

        loginButton.disabled = false;
        loginButton.textContent = "Login";
    }
});
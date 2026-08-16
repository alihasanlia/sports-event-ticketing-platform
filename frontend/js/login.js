const loginForm = document.getElementById("loginForm");
const loginButton = document.getElementById("loginButton");
const loginMessage = document.getElementById("loginMessage");

const API_BASE_URL = "http://localhost:8081";

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const email =
        document.getElementById("email").value.trim();

    const password =
        document.getElementById("password").value;

    const role =
        document.getElementById("role").value;

    if (!email || !password || !role) {
        loginMessage.textContent =
            "Please fill in all fields.";

        return;
    }

    loginButton.disabled = true;
    loginButton.textContent = "Logging in...";
    loginMessage.textContent = "";

    try {

        const response = await fetch(
            `${API_BASE_URL}/api/auth/login`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    email: email,
                    password: password,
                    role: role
                })
            }
        );

        const contentType =
            response.headers.get("content-type");

        let data = {};

        if (
            contentType &&
            contentType.includes("application/json")
        ) {
            data = await response.json();
        } else {
            const text = await response.text();

            data = {
                message: text
            };
        }

        if (!response.ok) {
            throw new Error(
                data.message ||
                `Login failed (${response.status}).`
            );
        }

        if (!data.token) {
            throw new Error(
                "Login succeeded but no token was returned."
            );
        }

        localStorage.setItem(
            "token",
            data.token
        );

        localStorage.setItem(
            "role",
            role
        );

        localStorage.setItem(
            "email",
            email
        );

        if (role === "USER") {
            window.location.href = "index.html";
        }

        else if (role === "SUPPORT") {
            window.location.href = "admin-index.html";
        }

    } catch (error) {

        console.error("Login error:", error);

        loginMessage.textContent =
            error.message ||
            "Something went wrong. Please try again.";

    } finally {

        loginButton.disabled = false;
        loginButton.textContent = "Login";
    }

});
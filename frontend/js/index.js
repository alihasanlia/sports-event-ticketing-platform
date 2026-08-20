/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const searchForm =
    document.getElementById("searchForm");

const searchInput =
    document.getElementById("searchInput");

const sportsGrid =
    document.querySelector(".sports-grid");

const sportsLoading =
    document.getElementById("sportsLoading");


/* =====================================================
   GET TOKEN
===================================================== */

function getToken() {

    return localStorage.getItem("token");
}


/* =====================================================
   AUTH HEADERS
===================================================== */

function getAuthHeaders() {

    const token = getToken();

    return {

        "Content-Type": "application/json",

        ...(token ? { "Authorization": `Bearer ${token}` } : {})

    };
}


/* =====================================================
   SEARCH
===================================================== */

searchForm.addEventListener("submit", function (event) {

    event.preventDefault();

    const query =
        searchInput.value.trim();

    if (query === "") {
        return;
    }

    window.location.href =
        "results.html?q=" +
        encodeURIComponent(query);
});


/* =====================================================
   LOAD SPORTS
===================================================== */

async function loadSports() {

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/sports`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        if (!response.ok) {

            throw new Error(
                "Could not load sports."
            );
        }


        const sports =
            await response.json();


        console.log(
            "Sports:",
            sports
        );


        // Remove loading
        sportsLoading.style.display =
            "none";


        if (sports.length === 0) {

            sportsGrid.innerHTML += `
                <div style="grid-column: 1 / -1; text-align: center; padding: 2rem;">
                    <p>No sports available.</p>
                </div>
            `;

            return;
        }


        // Display sports
        displaySports(sports);


    } catch (error) {

        console.error(
            "Load sports error:",
            error
        );

        sportsLoading.textContent =
            "Could not load sports. Please try again.";

        sportsLoading.style.color =
            "#e74c3c";
    }
}


/* =====================================================
   DISPLAY SPORTS
===================================================== */

function displaySports(sports) {

    const sportIcons = {
        "FOOTBALL": "⚽",
        "BASKETBALL": "🏀",
        "VOLLEYBALL": "🏐",
        "TENNIS": "🎾",
        "SWIMMING": "🏊",
        "ATHLETICS": "🏃",
        "CYCLING": "🚴",
        "SKIING": "⛷️"
    };


    sports.forEach(
        function (sport) {

            // Remove loading
            const loadingElement =
                document.getElementById(
                    "sportsLoading"
                );

            if (loadingElement) {

                loadingElement.remove();
            }


            const card =
                document.createElement("a");

            // pass sport identifier as enum-like value (backend expects SPORTTYPE e.g. FOOTBALL)
            const sportParam = (sport.sportType || sport.name || '').toString().toUpperCase();
            card.href = `sports.html?sport=${encodeURIComponent(sportParam)}`;

            card.className =
                "sport-card";


            const icon =
                sportIcons[sport.name] ||
                "🏆";


            card.innerHTML = `

                <div class="sport-icon">
                    ${icon}
                </div>

                <h3>
                    ${sport.name.charAt(0).toUpperCase() + sport.name.slice(1).toLowerCase()}
                </h3>

                <p>
                    ${sport.description || `Explore ${sport.name} matches and tickets.`}
                </p>

            `;


            sportsGrid.appendChild(
                card
            );

        }
    );
}


/* =====================================================
   MATCH CALENDAR
===================================================== */

const calendarDays =
    document.getElementById(
        "calendarDays"
    );

const currentMonth =
    document.getElementById(
        "currentMonth"
    );

const previousMonth =
    document.getElementById(
        "previousMonth"
    );

const nextMonth =
    document.getElementById(
        "nextMonth"
    );

/*
* Start from the current date.
*/

let calendarDate = new Date();

/*
* Month names.
*/

const monthNames = [

    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"

];

/*
* Generate calendar.
*/

function generateCalendar() {

    calendarDays.innerHTML = "";


    const year =
        calendarDate.getFullYear();


    const month =
        calendarDate.getMonth();


    /*
     * First day of current month.
     *
     * 0 = Sunday
     * 1 = Monday
     * ...
     * 6 = Saturday
     */

    const firstDay =
        new Date(
            year,
            month,
            1
        ).getDay();


    /*
     * Number of days in current month.
     */

    const numberOfDays =
        new Date(
            year,
            month + 1,
            0
        ).getDate();



    /*
     * Update month title.
     */

    currentMonth.textContent =
        monthNames[month] +
        " " +
        year;



    /*
     * Add empty cells before
     * the first day.
     */

    for (
        let i = 0;
        i < firstDay;
        i++
    ) {

        const emptyDay =
            document.createElement("div");

        emptyDay.classList.add(
            "calendar-day",
            "empty"
        );

        calendarDays.appendChild(
            emptyDay
        );

    }



    /*
     * Current date.
     */

    const today =
        new Date();


    const todayYear =
        today.getFullYear();


    const todayMonth =
        today.getMonth();


    const todayDate =
        today.getDate();



    /*
     * Generate actual days.
     */

    for (
        let day = 1;
        day <= numberOfDays;
        day++
    ) {

        const dayElement =
            document.createElement("div");


        dayElement.classList.add(
            "calendar-day"
        );


        dayElement.textContent =
            day;



        /*
         * Highlight today.
         */

        if (

            year === todayYear &&
            month === todayMonth &&
            day === todayDate

        ) {

            dayElement.classList.add(
                "today"
            );

        }



        /*
         * Click on date.
         */

        dayElement.addEventListener(
            "click",
            function () {

                /*
                 * Create date in YYYY-MM-DD
                 * format.
                 */

                const selectedDate =
                    year +
                    "-" +
                    String(
                        month + 1
                    ).padStart(2, "0") +
                    "-" +
                    String(day)
                        .padStart(2, "0");


                /*
                 * Send selected date
                 * to results.html.
                 *
                 * Example:
                 *
                 * results.html?date=2026-08-15
                 */

                window.location.href =
                    "results.html?date=" +
                    selectedDate;

            }
        );


        calendarDays.appendChild(
            dayElement
        );

    }

}

/* =====================================================
PREVIOUS MONTH
===================================================== */

previousMonth.addEventListener(
    "click",
    function () {

        calendarDate.setMonth(
            calendarDate.getMonth() - 1
        );

        generateCalendar();

    }

);

/* =====================================================
NEXT MONTH
===================================================== */

nextMonth.addEventListener(
    "click",
    function () {

        calendarDate.setMonth(
            calendarDate.getMonth() + 1
        );

        generateCalendar();

    }

);

/* =====================================================
INITIALIZE
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        generateCalendar();
        loadSports();

    }
);
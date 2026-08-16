/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const sportTitle =
    document.getElementById("sportTitle");

const sportDescription =
    document.getElementById("sportDescription");

const sportLoading =
    document.getElementById("sportLoading");

const stadiumsGrid =
    document.getElementById("stadiumsGrid");

const leaguesGrid =
    document.getElementById("leaguesGrid");

const noItems =
    document.getElementById("noItems");

const toggleButtons =
    document.querySelectorAll(
        ".sport-toggle-btn"
    );

const stadiumsView =
    document.getElementById(
        "stadiumsView"
    );

const leaguesView =
    document.getElementById(
        "leaguesView"
    );


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
   GET URL PARAMETERS
===================================================== */

const urlParams =
    new URLSearchParams(
        window.location.search
    );

const sport =
    urlParams.get("sport");


/* =====================================================
   SPORT INFORMATION
===================================================== */

const sportInfo = {

    football: {

        title: "Football",

        description:
            "Browse football stadiums and leagues.",

        sportType: "FOOTBALL"

    },


    basketball: {

        title: "Basketball",

        description:
            "Browse basketball stadiums and leagues.",

        sportType: "BASKETBALL"

    },


    volleyball: {

        title: "Volleyball",

        description:
            "Browse volleyball stadiums and leagues.",

        sportType: "VOLLEYBALL"

    }

};


/* =====================================================
   GET SPORT ID BY NAME
===================================================== */

async function getSportIdByName(sportName) {

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/sports/name/${sportName}`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );

        if (!response.ok) {

            console.error(
                "Could not get sport ID:",
                response.status
            );

            return null;
        }

        const sportData =
            await response.json();

        return sportData.id;

    } catch (error) {

        console.error(
            "Get sport ID error:",
            error
        );

        return null;
    }
}


/* =====================================================
   LOAD SPORT DATA
===================================================== */

async function loadSportData() {

    // Update page title
    if (sport && sportInfo[sport]) {

        sportTitle.textContent =
            sportInfo[sport].title;

        sportDescription.textContent =
            sportInfo[sport].description;

    } else {

        sportTitle.textContent =
            "Sport Not Found";

        sportDescription.textContent =
            "Please select a valid sport.";

        sportLoading.textContent =
            "Sport not found";

        return;
    }


    sportLoading.style.display = "block";
    stadiumsGrid.innerHTML = "";
    leaguesGrid.innerHTML = "";
    noItems.style.display = "none";


    try {

        const sportName =
            sportInfo[sport].sportType;

        // Get Sport ID from name
        const sportId =
            await getSportIdByName(sportName);

        if (!sportId) {

            sportLoading.textContent =
                "Could not find sport.";

            sportLoading.style.color =
                "#e74c3c";

            return;
        }


        console.log(
            "Sport ID:",
            sportId
        );


        // Load leagues for this sport using /sport/{sportId}
        const leaguesResponse =
            await fetch(
                `${API_BASE_URL}/api/v1/leagues/sport/${sportId}`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        let leagues = [];

        if (leaguesResponse.ok) {

            leagues =
                await leaguesResponse.json();
        }


        console.log(
            "Leagues:",
            leagues
        );


        // Load tournaments for this sport using /sport/{sportId}
        const tournamentsResponse =
            await fetch(
                `${API_BASE_URL}/api/v1/tournaments/sport/${sportId}`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        let tournaments = [];

        if (tournamentsResponse.ok) {

            tournaments =
                await tournamentsResponse.json();
        }


        console.log(
            "Tournaments:",
            tournaments
        );


        // Load stadiums - we'll get them from matches
        const matchesResponse =
            await fetch(
                `${API_BASE_URL}/api/v1/matches/sport/${sportName}`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        let stadiums = [];

        if (matchesResponse.ok) {

            const matches =
                await matchesResponse.json();

            // Extract unique stadiums
            const stadiumMap = {};

            matches.forEach(match => {

                if (match.stadium && match.stadiumId) {

                    if (!stadiumMap[match.stadiumId]) {

                        stadiumMap[match.stadiumId] = {

                            id: match.stadiumId,

                            name: match.stadium,

                            city: match.city || "Unknown"

                        };
                    }
                }
            });

            stadiums =
                Object.values(stadiumMap);
        }


        console.log(
            "Stadiums:",
            stadiums
        );


        sportLoading.style.display =
            "none";


        // Display stadiums
        if (stadiums.length > 0) {

            displayStadiums(stadiums);

        } else {

            stadiumsGrid.innerHTML =
                `<div style="grid-column: 1 / -1; text-align: center; padding: 2rem;">
                    No stadiums available for this sport.
                </div>`;
        }


        // Display leagues (combine leagues and tournaments)
        const allLeagues = [...leagues, ...tournaments];

        if (allLeagues.length > 0) {

            displayLeagues(allLeagues);

        } else {

            leaguesGrid.innerHTML =
                `<div style="grid-column: 1 / -1; text-align: center; padding: 2rem;">
                    No leagues or tournaments available for this sport.
                </div>`;
        }


    } catch (error) {

        console.error(
            "Load sport data error:",
            error
        );

        sportLoading.textContent =
            "Could not load data. Please try again.";

        sportLoading.style.color =
            "#e74c3c";
    }
}


/* =====================================================
   DISPLAY STADIUMS
===================================================== */

function displayStadiums(stadiums) {

    stadiumsGrid.innerHTML = "";


    stadiums.forEach(
        function (stadium) {

            const card =
                document.createElement("a");

            card.href =
                `results.html?type=stadium&id=${stadium.id}`;

            card.className =
                "sport-item-card";


            card.innerHTML = `

                <div class="sport-item-icon">
                    🏟️
                </div>

                <div class="sport-item-content">

                    <h2>
                        ${stadium.name}
                    </h2>

                    <p>
                        ${stadium.city || "Unknown"}
                    </p>

                    <span class="item-link">
                        View Matches →
                    </span>

                </div>

            `;


            stadiumsGrid.appendChild(
                card
            );

        }
    );
}


/* =====================================================
   DISPLAY LEAGUES
===================================================== */

function displayLeagues(leagues) {

    leaguesGrid.innerHTML = "";


    leagues.forEach(
        function (league) {

            const card =
                document.createElement("a");

            // Check if it's a league or tournament
            const isTournament =
                league.tournamentType ||
                league.startDate ||
                false;

            const linkType =
                isTournament ? "tournament" : "league";

            card.href =
                `results.html?type=${linkType}&id=${league.id}`;

            card.className =
                "sport-item-card";


            const icon =
                isTournament ? "🏆" : "🏆";


            const country =
                league.country ||
                league.location ||
                "International";


            card.innerHTML = `

                <div class="sport-item-icon">
                    ${icon}
                </div>

                <div class="sport-item-content">

                    <h2>
                        ${league.name}
                    </h2>

                    <p>
                        ${country}
                        ${isTournament ? ` • ${league.tournamentType || "Tournament"}` : ""}
                    </p>

                    <span class="item-link">
                        View Matches →
                    </span>

                </div>

            `;


            leaguesGrid.appendChild(
                card
            );

        }
    );
}


/* =====================================================
   TOGGLE VIEWS
===================================================== */

toggleButtons.forEach(
    function (button) {

        button.addEventListener(
            "click",
            function () {

                toggleButtons.forEach(
                    function (btn) {

                        btn.classList.remove(
                            "active"
                        );
                    }
                );

                this.classList.add(
                    "active"
                );

                const view =
                    this.dataset.view;

                if (view === "stadiums") {

                    stadiumsView.classList.add(
                        "active"
                    );

                    leaguesView.classList.remove(
                        "active"
                    );

                } else {

                    leaguesView.classList.add(
                        "active"
                    );

                    stadiumsView.classList.remove(
                        "active"
                    );
                }

            }
        );

    }
);


/* =====================================================
   INITIALIZE
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadSportData();

    }
);
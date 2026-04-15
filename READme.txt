====================================================
PROJECT: SMART LANDMARKS APP (ID: 22301531)
====================================================

1. PROJECT OVERVIEW
This is an Android application built for my Lab Exam. The main goal is to help 
users find and track famous landmarks across Bangladesh. It uses a clean, 
tabbed interface to switch between a map view and a list view, ensuring 
users can interact with the data however they prefer.

2. FEATURES IMPLEMENTED
- Bottom Navigation: Quick switching between Map, Landmarks, and Profile.
- Interactive Map: Google Maps integration with custom markers for landmarks.
- Dynamic List: A RecyclerView that loads landmark titles and images from a remote API.
- Detail View: Users can view specific info about a landmark's popularity and score.
- Error Handling: Basic checks for internet connectivity and API response issues.

3. API USAGE
The app communicates with a PHP-based backend at:
https://labs.anontech.info/cse489/exm3/api.php
I used Retrofit with GSON for network calls. The "get_landmarks" action 
fetches the full list of data, and the student ID key (22301531) is 
passed as a query parameter for authentication.

4. OFFLINE STRATEGY
For the offline requirement, I implemented a Room Database. The logic 
is designed to cache API results locally. When the app starts, it 
attempts to refresh the data from the server, but if the network is 
unavailable, it falls back to the local SQLite storage so the list 
is never empty.

5. ARCHITECTURE USED
I stuck with a Fragment-based architecture controlled by a single 
MainActivity. I used an Adapter pattern for the list components to keep 
the code modular. For data fetching, I used Kotlin Coroutines to 
keep the UI thread smooth while waiting for network responses.

6. CHALLENGES FACED
The biggest headache was the Gradle configuration for 
the Kapt/KSP plugins. There were some version conflicts between 
the latest Android Studio build and the Room compiler that took a 
while to debug. I also spent quite a bit of time getting the marker 
colors to change dynamically on the map based on the landmark scores.
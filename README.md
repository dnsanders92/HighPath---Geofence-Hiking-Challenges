# HighPath: Geofence Hiking Challenges

An Android app for discovering hiking trails, recording hikes, and completing summit challenges. Built in Java as my final year project for a BSc (Hons) in Computer Science at Ulster University (February – May 2026), and refactored in October 2026.

## 🌟 What It Does

* **Record a Hike:** A foreground service tracks your location and shows live time, distance, elevation, and pace while you walk. The route is saved as a standard GPX file.
* **Summit Challenges:** When an activity finishes, the app scans the GPX track points against a list of summits. Passing within 15 metres of a summit marks it as completed and updates progress on that region's challenge. Activities that reach a summit are automatically named after it.
* **Trail Discovery:** Browse trails stored in the app, view them drawn on a Mapbox map, and load one onto the recording map to follow it.
* **Activity Breakdown:** Each completed activity features final metrics, per-kilometre splits, and an estimated calorie count. Activities can be edited or deleted.
* **Weather:** Features a 5-day forecast for each summit powered by the OpenWeather API.
* **Accounts & Sync:** Users sign up and log in through Supabase. Activities, splits, challenge progress, and trail map images are saved locally and synced to the cloud, with GPX files backed up to cloud storage. Logging in on any device fully restores the user's data.
* **Search & Profile:** Live search across the Explore, Activities, and Challenges screens, plus a profile page showing aggregate stats alongside uploadable profile and banner images.

---

## 🛠️ Tech Stack

| Area | Tools & Libraries |
| :--- | :--- |
| **Language** | Java |
| **Platform** | Android (Android Studio, Gradle) |
| **Local Storage** | Room Database |
| **Cloud & Backend** | Supabase (Database, Authentication, Storage) |
| **Maps & Location** | Mapbox SDK |
| **APIs** | OpenWeather API, Open Topo API |
| **Navigation** | Bottom Navigation (Explore, Activities, Record, Challenges, Profile) |

---

## 📐 Design Notes

* **Foreground Recording Service:** Time, metrics (total distance and elevation), and pace are each handled by their own `Runnable` inside a foreground service. This ensures live stats stay accurate while the app runs in the background. Isolating these into their own package keeps the main service class clean and readable.
* **Elevation Correction:** Raw GPS elevation is inherently noisy. Finished activities have their elevation data corrected via the Open Topo API. Because the API limits requests to 100 locations at a time, elevation data is processed in batches before sending.
* **Geofence Check:** Summit detection runs against the recorded track post-activity, comparing each GPX point against summits stored in the local database.
* **Offline-First Architecture:** Data is written directly to the local Room database before syncing asynchronously to Supabase, guaranteeing fast offline access with cloud backup.
* **Testing:** Comprehensive unit tests cover pace, distance, and elevation calculations, GPX parsing, and Room database CRUD operations for users, activities, and splits.

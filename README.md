# HighPath: Geofence Hiking Challenges

An Android app for discovering hiking trails, recording hikes, and completing summit challenges. Built in Java as my final year project for a BSc (Hons) in Computer Science at Ulster University (February – May 2026), and refactored in October 2026.

## 📸 Screenshots

<p align="center">
  <img width="30%" src="https://github.com/user-attachments/assets/760c63ed-a337-494b-920a-bcfc04bdab91" />
  <img width="30%" src="https://github.com/user-attachments/assets/b3dec186-bc9f-4214-87cc-54e73464380c" />
  <img width="30%" src="https://github.com/user-attachments/assets/dd0582d5-94cd-4c40-9870-2a5e74101f40" />
</p>
<p align="center">
  <img width="30%" src="https://github.com/user-attachments/assets/4fd61f06-b847-4268-bbc5-3ea18861c1ca" />
  <img width="30%" src="https://github.com/user-attachments/assets/a4125bb2-b581-4901-9afc-42483c7c2f71" />
  <img width="30%" src="https://github.com/user-attachments/assets/2a99903d-aa61-456c-890d-313628f61db6" />
</p>
<p align="center">
  <img width="30%" src="https://github.com/user-attachments/assets/812034b6-f5e6-439c-8d4f-bf33951e34d7" />
  <img width="30%" src="https://github.com/user-attachments/assets/9b82db20-4383-4e4a-b676-d3a510ff2de1" />
  <img width="30%" src="https://github.com/user-attachments/assets/6efaec51-0fe6-4c39-b160-3ae8318cd941" />
</p>

> *Note: Elevation tracking and correction are fully functional in the live app; these screenshots were captured during testing using a location emulator.*

---

## 🌟 What It Does

- **Record a Hike:** A foreground service tracks your location and shows live time, distance, elevation, and pace while you walk. The route is saved as a standard GPX file.
- **Summit Challenges:** When an activity finishes, the app scans the GPX track points against a list of summits. Passing within 15 metres of a summit marks it as completed and updates progress on that region's challenge. Activities that reach a summit are automatically named after it.
- **Trail Discovery:** Browse trails stored in the app, view them drawn on a Mapbox map, and load one onto the recording map to follow it.
- **Activity Breakdown:** Each completed activity features final metrics, per-kilometre splits, and an estimated calorie count. Activities can be edited or deleted.
- **Weather:** Features a 5-day forecast for each summit powered by the OpenWeather API.
- **Accounts & Sync:** Users sign up and log in through Supabase. Activities, splits, challenge progress, and trail map images are saved locally and synced to the cloud, with GPX files backed up to cloud storage. Logging in on any device fully restores the user's data.
- **Search & Profile:** Live search across the Explore, Activities, and Challenges screens, plus a profile page showing aggregate stats alongside uploadable profile and banner images.

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

- **Foreground Recording Service:** Time, metrics (total distance and elevation), and pace are each handled by their own `Runnable` inside a foreground service. This ensures live stats stay accurate while the app runs in the background. Isolating these into their own package keeps the main service class clean and readable.
- **Elevation Correction:** Raw GPS elevation is inherently noisy. Finished activities have their elevation data corrected via the Open Topo API. Because the API limits requests to 100 locations at a time, elevation data is processed in batches before sending.
- **Geofence Check:** Summit detection runs against the recorded track post-activity, comparing each GPX point against summits stored in the local database.
- **Offline-First Architecture:** Data is written directly to the local Room database before syncing asynchronously to Supabase, guaranteeing fast offline access with cloud backup.
- **Testing:** Comprehensive unit tests cover pace, distance, and elevation calculations, GPX parsing, and Room database CRUD operations for users, activities, and splits.

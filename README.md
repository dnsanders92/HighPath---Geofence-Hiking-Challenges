# HighPath: Geofence Hiking Challenges

An Android app for discovering hiking trails, recording hikes, and completing summit challenges. Built in Java as my final year project for a BSc (Hons) in Computer Science at Ulster University (February – May 2026), and refactored in October 2026.

## 📸 App Screenshots

<table>
  <tr>
    <td align="center">
      <img width="220" alt="activities_tab" src="https://github.com/user-attachments/assets/df883277-dba9-4b81-aa32-3284dfcc3d22" /><br/>
      <b>Activities Tab</b><br/>View completed hikes & metrics
    </td>
    <td align="center">
      <img width="220" alt="trail_discovery_tab" src="https://github.com/user-attachments/assets/b56adef6-e798-4735-bded-191eec3369a7" /><br/>
      <b>Trail Discovery</b><br/>Browse trails on Mapbox
    </td>
    <td align="center">
      <img width="220" alt="trail_details_tab_with_5dayweather" src="https://github.com/user-attachments/assets/4d299be1-5db8-410f-bf64-c1d72695ccc2" /><br/>
      <b>Trail Details & Weather</b><br/>5-day summit forecast
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="220" alt="recording_live_tab_3dmap" src="https://github.com/user-attachments/assets/8d6d7a53-e718-490c-88b9-6dc5a673b19b" /><br/>
      <b>Live Recording (3D Map)</b><br/>Tracking hike in 3D
    </td>
    <td align="center">
      <img width="220" alt="recording_live_2dmap" src="https://github.com/user-attachments/assets/c8bac9ba-d517-488e-a125-06e15af19d41" /><br/>
      <b>Live Recording (2D Map)</b><br/>Tracking hike in 2D
    </td>
    <td align="center">
      <img width="220" alt="mountain_details_tab" src="https://github.com/user-attachments/assets/51fa8b38-9953-4f42-a23c-69eb70db74d3" /><br/>
      <b>Mountain Details</b><br/>Summit overview
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="220" alt="challenge_tab" src="https://github.com/user-attachments/assets/fb3b62ea-1762-4066-80f7-532353191345" /><br/>
      <b>Challenges Tab</b><br/>Regional challenge progress
    </td>
    <td align="center" colspan="2">
      <img width="220" alt="challenge_details_tab" src="https://github.com/user-attachments/assets/51a2f7bc-7921-4c31-aaea-e0548f57aea3" /><br/>
      <b>Challenge Details</b><br/>Specific summit checklist
    </td>
  </tr>
</table>

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

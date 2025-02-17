# StockScreener

## 📌 Setup Instructions

### Prerequisites

Ensure you have the following installed:

- **Android Studio** (Latest version)
- **Java 17+**
- **Gradle** 
- **Git**

### Steps to Run the Project

1. **Clone the repository:**
   ```sh
   git clone https://github.com/sakinasawal/StockScreener.git
   ```
2. **Open the project in Android Studio.**
3. **Sync Gradle** to install dependencies.
4. **Run the project** using the `Run` button

Recommended: For the best experience, try this app on an Android device or emulator.

---

## 📌 Architecture Overview

The project follows the **MVVM (Model-View-ViewModel) architecture**, ensuring a clean separation of concerns and maintainability.

### **Project Structure:**

```
/app/src/main/java/com/example/stockscreener
│── network/         # Repository & API calls
│── data/            # Data models(Entities for Room & API response models)
│── storage/         # Room Database
│── dao/             # Dao interface
│── stock/           # Jetpack Compose UI components
│── viewmodel/       # ViewModels for managing UI state
│── Component/       # Helper functions & extensions
│── MainActivity/    # Main UI
│── Screen/          # Handle navigation UI
│── NoNetworkBanner/ # No network UI
│── Dimension/       # Helper size UI
│── ui/              # theme 
```

### **Technologies Used:**

- **Jetpack Compose** – UI toolkit for building native Android UI.
- **Kotlin** – Main programming language.
- **MVVM Architecture** – For better separation of concerns.
- **Retrofit** – For API requests to Alpha Vantage.
- **Room Database** – For local data storage.
- **StateFlow** – For state management in ViewModel.
- **Coroutines** – For asynchronous operations.

---

## 📌 Future Improvement Ideas

- **Implement Pagination** – Optimize API calls to handle large datasets efficiently.
- **Add Unit Testing** – Increase test coverage for ViewModel and repository layers.
- **Improve UI/UX** – Enhance UI screen with interactive charts.
- **CI/CD Integration** – Automate testing and deployment using GitHub Actions.

---


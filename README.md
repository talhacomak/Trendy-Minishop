# 📱 Trendy MiniShop

**Trendy MiniShop** is a modern e-commerce showcase app built with **Jetpack Compose** and **Clean Architecture** and **Modular architecture principles**.
It demonstrates how to build a scalable shopping experience with **drag & drop cart animations**, **favorites**, and **modular domain-data-UI separation**.


## ✨ Features

* 🛍️ **Home screen with products & categories**
* ❤️ **Favorites management** (toggle & observe)
* 🧲 **Drag & Drop Add-to-Cart interaction** with smooth animations
* 💫 **Animated CartDropArea** (pulse, scale, overlap detection)
* 🧩 **Composable UI with clean state management** (`homeUiState`)
* 📦 **Offline-first repository pattern** (local + remote sources)
* 🧪 **Unit tests** (Flow, repository logic) + **Paparazzi screenshot tests**


## 🏗️ Architecture

The project follows **Clean Architecture** and **modular multi-module** design:

TrendyMiniShop/
 ├── app/                        # Application module (entry point, Hilt setup)
 ├── core/
 │   ├── common/                 # Utility & shared classes
 │   ├── database/               # Local Room entities, DAOs, migrations
 │   └── network/                # Remote data sources & API DTOs
 ├── domain/                     # Business logic (UseCases, Models, Interfaces)
 └── feature/
     ├── home/                   # Home screen, product grid, drag-drop logic
     │   ├── drag/               # CartDropArea, DraggableProductCard, state
     │   └── HomeViewModel.kt    # UI logic, flows, and event handling
     ├── favorites/              # Favorites screen & domain integration
     └── cart/                   # Cart screen (planned)



### 🧠 Layered structure

| Layer            | Responsibility                           | Example                                           |
| ---------------- | ---------------------------------------- | ------------------------------------------------- |
| **UI (Compose)** | Declarative components, animation, state | `CartDropArea.kt`, `HomeScreen.kt`                |
| **Domain**       | Business rules, pure Kotlin use-cases    | `AddToCartUseCase.kt`, `ToggleFavoriteUseCase.kt` |
| **Data**         | Repository pattern, Room + Retrofit      | `ProductRepositoryImpl.kt`                        |
| **Core**         | Shared utilities & base logic            | `MainDispatcherRule.kt`, `BaseResponse.kt`        |


## ⚙️ Tech Stack

| Category | Technology                                     |
| -------- | ---------------------------------------------- |
| UI       | Jetpack Compose, Material 3                    |
| State    | MutableState + ViewModel (Hilt injected)       |
| DI       | Dagger Hilt                                    |
| Async    | Kotlin Coroutines, Flow                        |
| Data     | Room, Retrofit (or mock remote source)         |
| Test     | JUnit, Turbine, Truth, Mockito, Paparazzi      |
| Build    | Gradle Version Catalogs (`libs.versions.toml`) |


## 🎬 Highlight: Drag & Drop Cart Animation

* Built with `detectDragGesturesAfterLongPress()`
* Reversible **scale + spring animations** on release
* Cart area proximity detection using custom **CartDropController**
* “Pulse” feedback when an item is successfully dropped

## 🧪 Testing

### Unit Tests

Located in `feature/home` and `domain/` modules

* Flow testing via `app.cash.turbine.test`
* Mockito-Kotlin for dependency mocking
* `MainDispatcherRule` for coroutine test context

### UI Screenshot Tests

* Implemented with **Paparazzi**
* Captures composable previews to ensure design consistency



## 🚀 Getting Started

### Prerequisites

* **Android Studio Ladybug+**
* **JDK 17+**
* **Gradle 8.7+**



## 🧩 Future Roadmap

* 🛒 Complete CartScreen with persistent quantities
* 🔍 Product detail (PDP) with Compose navigation
* 🧠 Offline caching and paging integration
* 🌐 Remote API backend & authentication layer
* 📊 Analytics module




## 📸 Screenshots

| Home | Dragging | Added to Cart |
| ---- | -------- | ------------- |
| 🖼️  | 🖼️      | 🖼️           |



## 👨‍💻 Author

**Talha Çomak**
Senior Android Engineer / Compose Enthusiast
📍 Istanbul, Türkiye
🔗 [GitHub](https://github.com/talhacomak) • [LinkedIn](https://linkedin.com/in/talha-comak-32-34-32-)



## 🧾 License

This project is licensed under the [MIT License](./LICENSE) —  
feel free to use, modify, and distribute with attribution.

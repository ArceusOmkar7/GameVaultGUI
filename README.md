# GameVault

## Project Description

GameVault is a simple application designed to manage a digital game library. It allows users to browse games, add them to a shopping cart, place orders, and view their transaction history. It also includes a basic administration view for managing games and users. The application persists data using a MySQL database.

The project consists of a core backend logic layer, a Command-Line Interface (CLI) application, and a basic Graphical User Interface (GUI) application.

## Key Features

*   **User Management:** Create users, view user information. (Basic CLI/GUI implementation)
*   **Game Management:** Add games, list games. (Basic CLI/GUI implementation, Admin functionality planned)
*   **Shopping Cart:** Add games to a cart, view cart contents, remove games. (Implemented in CLI/GUI)
*   **Order Placement:** Checkout the shopping cart to create an order. (Implemented in CLI/GUI)
*   **Transaction Tracking:** Record purchase transactions. (Implemented in CLI/GUI)
*   **Data Persistence:** Uses MySQL database for storing all data.
*   **Command-Line Interface (CLI):** A text-based interface for interacting with the system.
*   **Graphical User Interface (GUI):** A multi-panel Swing application with a sidebar and dynamic content area.
*   **Role Selection:** Basic choice between "User" and "Admin" perspectives on GUI startup.

## Technologies Used

*   **Language:** Java
*   **GUI Framework:** Swing
*   **Database:** MySQL
*   **Database Connectivity:** JDBC
*   **Build Tool:** Apache Ant (via `build.xml` and NetBeans project files)

## Project Structure
```
.
├── build.xml                     # Apache Ant build file
├── build/                        # Compiled class files
│   └── classes/
│       └── com/
│           └── project/
│               ├── gamevaultcli/ # CLI compiled classes
│               │   ├── GameVaultCLI.class
│               │   ├── entities/
│               │   ├── exceptions/
│               │   ├── helpers/
│               │   ├── interfaces/
│               │   ├── management/
│               │   └── storage/
│               └── gamevaultgui/ # GUI compiled classes
│                   ├── BillingPanel.class
│                   ├── CartPanel.class
│                   ├── DashboardPanel.class
│                   ├── DatabaseConnectionPanel.class
│                   ├── GameFormDialog.class
│                   ├── GameVaultFrame.class
│                   ├── LoginPanel.class
│                   ├── ManageGamesPanel.class
│                   ├── ManageUsersPanel.class
│                   ├── NavbarPanel.class
│                   ├── SidebarPanel.class
│                   ├── SignupPanel.class
│                   ├── UserPanel.class
│                   ├── dialogs/
│                   └── panels/
├── nbproject/                    # NetBeans-specific project config files
│   ├── build-impl.xml
│   ├── genfiles.properties
│   ├── project.properties
│   ├── project.xml
│   └── private/
│       └── config.properties
└── src/
    └── com/
        └── project/
            ├── gamevaultcli/    # CLI version of the app
            │   ├── GameVaultCLI.java
            │   ├── entities/    # Core data models
            │   │   ├── Cart.java
            │   │   ├── Game.java
            │   │   ├── Order.java
            │   │   ├── Transaction.java
            │   │   └── User.java
            │   ├── exceptions/  # Custom exception classes
            │   │   ├── CartEmptyException.java
            │   │   ├── GameAlreadyOwnedException.java
            │   │   ├── GameNotFoundException.java
            │   │   ├── InvalidUserDataException.java
            │   │   ├── OrderNotFoundException.java
            │   │   └── UserNotFoundException.java
            │   ├── helpers/     # Utility/helper classes
            │   │   ├── DBUtil.java
            │   │   └── Helper.java
            │   ├── interfaces/  # Interface definitions
            │   ├── management/  # Business logic layer
            │   └── storage/     # Data access layer
            └── gamevaultgui/    # GUI version of the app
                ├── GameVaultFrame.java
                ├── dialogs/
                └── panels/
```

## Setup and Installation

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 8 or higher.
    *   MySQL Server installed and running.
    *   MySQL JDBC Connector JAR file (e.g., `mysql-connector-j-9.2.0.jar`).

2.  **Clone the repository:** (If applicable)
    ```bash
    git clone <repository_url>
    cd GameVaultCLI_DB # Or your project directory name
    ```

3.  **Add MySQL JDBC Connector:**
    *   Place the `mysql-connector-j-x.y.z.jar` file in a convenient location (e.g., within the project directory or a central libs folder).
    *   **If using NetBeans:** Open project properties -> Libraries -> Add JAR/Folder and select the connector JAR. This will update the `nbproject/project.properties` and `nbproject/build-impl.xml` files.
    *   **If using Ant directly:** Ensure the JAR is included in the `javac.classpath` and `run.classpath` properties in `nbproject/project.properties`.

4.  **Database Configuration:**
    *   The application (both CLI and GUI) will prompt you to enter the MySQL database name, username, and password on the first run.
    *   The user credentials provided must have permissions to `CREATE DATABASE` and `CREATE TABLE`.
    *   The application will automatically create the database (if it doesn't exist) and the necessary tables (`Users`, `Games`, `Carts`, `CartItems`, `Orders`, `Transactions`).

## How to Run

The project can be built and run using Apache Ant, as configured by NetBeans project files.

1.  **Build the project:**
    ```bash
    ant jar
    ```
    This will compile the code and build a JAR file in the `dist` directory.

2.  **Run the GUI application:**
    *   You can run the application directly using Ant:
        ```bash
        ant run
        ```
    *   Alternatively, if running from an IDE like NetBeans, simply run the project.

3.  **Run the CLI application (Optional):**
    *   Modify the project's main class in `nbproject/project.properties` to `com.project.gamevaultcli.GameVaultCLI` if you want to run the CLI version.

## Current Status (What's Done)

*   Complete backend data access (`storage` package) for all entities (Users, Games, Carts, CartItems, Orders, Transactions) interacting with MySQL via JDBC.
*   Complete backend business logic (`management` package) for core operations: user creation/listing/login, game adding/listing, cart management (add/remove/view), order placement (including transaction creation and wallet update), transaction listing.
*   Initial predefined user and game data is loaded on application startup.
*   Basic CLI interface covering most backend functionalities.
*   GUI framework using `BorderLayout` and `CardLayout` for panel switching.
*   Visually distinct `SidebarPanel` and `NavbarPanel` with basic styling.
*   `SidebarPanel` buttons to switch between main content panels.
*   `NavbarPanel` displaying brand name, current page title, and user greeting.
*   User authentication via `LoginPanel` and `SignupPanel`.
*   `DashboardPanel` displays summary stats and lists recent orders/transactions.
*   `CartPanel` displays cart items, calculates total, and has remove/checkout buttons.
*   `BillingPanel` displays past orders and transactions.
*   `UserPanel` displays basic information for the logged-in user.
*   Admin panels including `ManageGamesPanel` and `ManageUsersPanel`.
*   Database connection through `DatabaseConnectionPanel`.
*   `GameFormDialog` for adding/editing games.
*   Custom exception handling.

## Future Enhancements

*   **Improve User Experience:**
    *   Add more comprehensive error handling and user feedback.
    *   Enhance UI design and responsiveness.
*   **Additional Features:**
    *   Implement search and filtering functionalities for games and users.
    *   Add more detailed analytics in the dashboard.
    *   Include game ratings and review system.
*   **Security Enhancements:**
    *   Implement password hashing and stronger authentication.
    *   Add role-based access control with more granular permissions.
*   **Technical Improvements:**
    *   Add comprehensive unit tests for all components.
    *   Optimize database queries for better performance.
    *   Implement logging for better debugging and monitoring.

## Author

*   Omkar (Based on the project files)

---

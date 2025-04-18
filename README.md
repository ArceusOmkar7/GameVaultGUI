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
.
├── .gitignore
├── build.xml
├── nbproject
│   ├── build-impl.xml
│   ├── genfiles.properties
│   ├── project.properties
│   └── project.xml
└── src
    └── com
        └── project
            ├── gamevaultcli
            │   ├── GameVaultCLI.java
            │   ├── entities
            │   │   ├── Cart.java
            │   │   ├── Game.java
            │   │   ├── Order.java
            │   │   ├── Transaction.java
            │   │   └── User.java
            │   ├── exceptions
            │   │   ├── CartEmptyException.java
            │   │   ├── GameNotFoundException.java
            │   │   ├── InvalidUserDataException.java
            │   │   ├── OrderNotFoundException.java
            │   │   └── UserNotFoundException.java
            │   ├── helpers
            │   │   ├── DBUtil.java
            │   │   └── Helper.java
            │   ├── interfaces
            │   │   └── StorageInterface.java
            │   ├── management
            │   │   ├── CartManagement.java
            │   │   ├── GameManagement.java
            │   │   ├── GameVaultManagement.java
            │   │   ├── GameVaultMenu.java
            │   │   ├── OrderManagement.java
            │   │   ├── TransactionManagement.java
            │   │   └── UserManagement.java
            │   └── storage
            │       ├── CartStorage.java
            │       ├── GameStorage.java
            │       ├── OrderStorage.java
            │       ├── TransactionStorage.java
            │       └── UserStorage.java
            └── gamevaultgui
                ├── BillingPanel.java
                ├── CartPanel.java
                ├── DashboardPanel.java
                ├── GameVaultFrame.java
                ├── ManageGamesPanel.java   <-- New Admin Panel
                ├── ManageUsersPanel.java   <-- New Admin Panel
                ├── NavbarPanel.java
                ├── SidebarPanel.java
                └── UserPanel.java

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
    *   Modify the `main` method in `src/com/project/gamevaultcli/GameVaultCLI.java` to instantiate and make visible the `GameVaultFrame` instead of running the `GameVaultMenu`. (The provided `GameVaultFrame.java` includes a `main` method for testing, but the project's primary entry point is usually configured in `project.properties` and `build.xml` to point to `GameVaultCLI`).
    *   Once the main class is set to `com.project.gamevaultgui.GameVaultFrame` (in `nbproject/project.properties`), you can run using Ant:
        ```bash
        ant run
        ```
    *   Alternatively, if running from an IDE like NetBeans, simply run the project.

3.  **Run the CLI application (Optional):**
    *   Ensure the main class in `nbproject/project.properties` is set to `com.project.gamevaultcli.GameVaultCLI`.
    *   Run using Ant:
        ```bash
        ant run
        ```

## Current Status (What's Done)

*   Complete backend data access (`storage` package) for all entities (Users, Games, Carts, CartItems, Orders, Transactions) interacting with MySQL via JDBC.
*   Complete backend business logic (`management` package) for core operations: user creation/listing/login, game adding/listing, cart management (add/remove/view), order placement (including transaction creation and wallet update), transaction listing.
*   Initial predefined user and game data is loaded on application startup.
*   Basic CLI interface (`GameVaultMenu`) covering most backend functionalities.
*   Basic GUI framework (`GameVaultFrame`) using `BorderLayout` and `CardLayout` for panel switching.
*   Visually distinct `SidebarPanel` and `NavbarPanel` with basic styling resembling the target design.
*   `SidebarPanel` buttons to switch between main content panels.
*   `NavbarPanel` displaying brand name, current page title, and user greeting.
*   `DashboardPanel` displays summary stats and lists recent orders/transactions for the logged-in user or all for admin.
*   `CartPanel` displays cart items for the logged-in user, calculates total, and has remove/checkout buttons.
*   `BillingPanel` displays past orders and transactions for the logged-in user.
*   `UserPanel` displays basic information for the logged-in user.
*   Role selection panel on GUI startup to choose "User" or "Admin" perspective.
*   Sidebar button visibility dynamically updates based on the selected role (User sees Dashboard, Cart, Billing, User; Admin sees Dashboard, Manage Games, Manage Users).
*   `DBUtil` handles database and table creation on first run.

## Future Plans (What's Left)

*   **Implement Admin Panels:**
    *   Develop `ManageGamesPanel` with functionality to view, add, edit, and delete games using `GameManagement`.
    *   Develop `ManageUsersPanel` with functionality to view and potentially manage (e.g., delete, view details) users using `UserManagement`.
*   **Enhance User Panels:**
    *   Implement functionality in `UserPanel` to allow users to update their information or add funds to their wallet.
*   **Improve Game Browsing:**
    *   Implement a dedicated panel or add functionality to the Dashboard for users to browse all available games and add them to their cart directly (e.g., using a table or list).
    *   Add a "Buy Now" feature that bypasses the cart for a single item.
*   **Refine GUI Styling and Layout:**
    *   Improve the layout and appearance of all panels (`DashboardPanel`, `CartPanel`, `BillingPanel`, `UserPanel`, and the future admin panels) for better usability and visual appeal.
    *   Add actual icon images to the `SidebarPanel` buttons and `NavbarPanel` profile/admin indicator.
    *   Add better padding and spacing consistency.
*   **Error Handling and Feedback:**
    *   Implement more user-friendly error messages and feedback within the GUI (e.g., using status bars, specific error labels).
*   **Data Filtering and Sorting:**
    *   Add features to sort and filter data in tables (e.g., sorting games by price, filtering orders by date).
*   **Detailed Views:**
    *   Add functionality to view detailed information about a specific game, order, or transaction by clicking on table rows.
*   **Authentication:**
    *   Implement a proper user login and potentially admin login system instead of the current basic role selection.
*   **Unit Testing:**
    *   Add unit tests for the backend `management` classes to ensure logic correctness.

## Author

*   Omkar (Based on the project files)

---

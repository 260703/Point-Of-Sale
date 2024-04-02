# Point-Of-Sale

# Online Store Cashier System

## Overview
The Online Store Cashier System is a Java application that allows cashiers to manage customer purchases in a retail store. The application provides a graphical user interface (GUI) for adding products to a shopping cart, generating bills, and fetching recent bills from the database.

## Features
- Add products to the shopping cart by selecting from a dropdown menu or searching.
- Specify the quantity of each product.
- Generate bills for customer purchases.
- View recent bills from the database.
- Store product information and bill details in a MySQL database.

## Technologies Used
- Java
- Swing (for GUI)
- MySQL
- JDBC

## Getting Started
To run the application locally, follow these steps:
1. Clone the repository: `git clone https://github.com/your-username/online-store-cashier-system.git`
2. Set up the MySQL database by running the provided SQL script in the file named as `MysqlQuery.sql`.
3. Update the database connection details in the `DatabaseConnector.java` file.
4. Compile and run the `CashierPanel.java` file to launch the application.

## Usage
1. Launch the application.
2. Add products to the shopping cart by selecting from the dropdown menu or searching for them.
3. Specify the quantity of each product.
4. Click the "Add" button to add the product to the shopping cart.
5. Generate a bill by clicking the "Generate Bill" button.
6. View recent bills by clicking the "Fetch Bills" button.

## Database Structure
The MySQL database consists of two tables:
1. `products`: Stores product information, including product ID, name, and price.
2. `RecentBills`: Stores bill details, including a unique bill identifier, product ID, quantity, and price.

## Contributing
Contributions to the Online Store Cashier System project are welcome! If you have any ideas for new features, enhancements, or bug fixes, feel free to submit a pull request.



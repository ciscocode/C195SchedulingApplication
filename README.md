# Java Scheduling Application

This desktop application is a scheduling tool that allows you to manage customers and appointments for a fictitious business. It is built using Java and JavaFX for the user interface and connects to a MySQL database for data storage and retrieval.

**Author:** Francisco Montenegro

**Development Environment:**
- **IDE:** IntelliJ IDEA 2022.3.1 (Community Edition)
- **Java Version:** Java SE 17.0.5
- **Java FX SDK:** 17.0.2
- **MySQL Connector:** mysql-connector-java-8.0.31

## Getting Started

Follow these steps to recreate the database on your end and run the application:

### 1. Clone the Project

Clone this GitHub repository to your local machine:

```bash
git clone https://github.com/ciscocode/C195SchedulingApplication.git
```

### 2. Database Setup

Before you can run the application, you need to set up the MySQL database. You can do this using the provided SQL file and Entity Relationship Diagram (ERD) file.

Open MySQL Workbench or your preferred MySQL database management tool.
Create a new database with a suitable name (e.g., scheduling_app).
Importing the Database Schema:

Locate the SQL file in the project repository named sample_database_schema.sql.
Open the SQL file and copy its contents.
In your MySQL management tool, open a new SQL script window and paste the copied SQL code.
Execute the SQL script to create the necessary tables and schema for the application.

### 3. Update Database Connection Configuration

In the Java project, locate the database configuration file named JDBC. Update the connection details to match your MySQL database credentials (e.g., database URL, username, and password).

### 4. Run the Application

Open the project in IntelliJ IDEA:

Open IntelliJ IDEA.
Click on "File" > "Open" and select the project directory you cloned in step 1.
Let IntelliJ build and configure the project.
Once the project is loaded:

Locate the Application.java file
Right-click on it and select run to start the application.
You should now have the Java Scheduling Application up and running, connected to your MySQL database. You can use it to add and modify customers and appointments for your business.

Please note that this README assumes you have Java, JavaFX, and MySQL properly installed on your system. 

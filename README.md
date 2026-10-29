# Contact Management System

A simple full-stack Contact Management System built as an **MCA Advanced Internet Programming (AIP) Mini-Project**.

## 🛠️ Technologies Used

| Layer | Technology |
|---|---|
| Frontend | HTML, Tailwind CSS, JavaScript (Fetch API) |
| Backend | Java Servlet (Jakarta EE) |
| Database | MySQL |
| DB Connectivity | JDBC with PreparedStatement |
| Web Server | Apache Tomcat 11 |
| Build Tool | Maven |

## ✨ Features

- View all contacts in a table
- Add a new contact (name, phone, email)
- Edit an existing contact
- Delete a contact
- All operations use REST-style HTTP methods (GET, POST, PUT, DELETE)

## 📋 Prerequisites

Make sure you have these installed before running:

| Tool | Version |
|---|---|
| Java JDK | 17 or above |
| Apache Maven | 3.6 or above |
| MySQL | 8.0 or above |
| Apache Tomcat | 11.x |

## 🚀 How to Run

### Step 1 — Clone the repository
```bash
git clone https://github.com/Nithin-127/ContactManagementSystem_AIP_Project.git
cd ContactManagementSystem_AIP_Project
```

### Step 2 — Set up the database
```bash
mysql -u root -p < sql/contact_db.sql
```

### Step 3 — ⚠️ Update your MySQL credentials
Open `src/main/java/com/contact/DBConnection.java` and update lines 18–19:
```java
private static final String DB_USER     = "root";       // your MySQL username
private static final String DB_PASSWORD = "yourpassword"; // your MySQL password
                                                          // use "" if no password
```

### Step 4 — Build the project
```bash
mvn clean package
```

### Step 5 — Deploy to Tomcat
Copy the WAR file to your Tomcat webapps folder:
```bash
cp target/ContactManagementSystem.war /path/to/tomcat/webapps/
```

### Step 6 — Start Tomcat and open the app
Start Tomcat, then open in browser:
```
http://localhost:8080/ContactManagementSystem/
```

## 📁 Project Structure

```
ContactManagementSystem/
├── pom.xml                          # Maven configuration
├── sql/
│   └── contact_db.sql               # Run this to create the database
└── src/main/
    ├── java/com/contact/
    │   ├── DBConnection.java         # ⚠️ Update MySQL credentials here
    │   └── ContactServlet.java       # Handles GET, POST, PUT, DELETE
    └── webapp/
        ├── WEB-INF/web.xml           # Servlet descriptor
        └── index.html                # Frontend UI
```

## 📡 API Endpoints

| Method | URL | Action |
|---|---|---|
| GET | `/api/contacts` | Get all contacts |
| POST | `/api/contacts` | Add a new contact |
| PUT | `/api/contacts` | Update a contact |
| DELETE | `/api/contacts?id=1` | Delete a contact |

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

## 📁 Project Structure

```
ContactManagementSystem/
├── pom.xml                          # Maven configuration
├── sql/
│   └── contact_db.sql               # Database setup script
└── src/main/
    ├── java/com/contact/
    │   ├── DBConnection.java         # JDBC database connection
    │   └── ContactServlet.java       # Servlet handling all HTTP methods
    └── webapp/
        ├── WEB-INF/web.xml           # Servlet descriptor
        └── index.html                # Frontend UI
```

## 🚀 How to Run

### 1. Set up the database
```bash
mysql -u root < sql/contact_db.sql
```

### 2. Update DB credentials
Open `src/main/java/com/contact/DBConnection.java` and set your MySQL username and password.

### 3. Build the project
```bash
mvn clean package
```

### 4. Deploy to Tomcat
Copy `target/ContactManagementSystem.war` to your Tomcat `webapps/` folder.

### 5. Start Tomcat and open the app
```
http://localhost:8080/ContactManagementSystem/
```

## 📡 API Endpoints

| Method | URL | Action |
|---|---|---|
| GET | `/api/contacts` | Get all contacts |
| POST | `/api/contacts` | Add a new contact |
| PUT | `/api/contacts` | Update a contact |
| DELETE | `/api/contacts?id=1` | Delete a contact |

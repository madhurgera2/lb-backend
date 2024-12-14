# Life Bridge Backend

## 🌍 Project Overview
Life Bridge is a comprehensive platform for managing organ requests and donations, facilitating a streamlined process for medical professionals and potential donors.

## 🚀 Features
- Organ Request Management
- Organ Donation Tracking
- Role-based Access Control
- Secure Authentication
- Flexible Filtering and Reporting

## 📋 Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.8+

## 🔧 Installation Guide

### 1. Database Setup

#### Ubuntu
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation

# Create database and user
mysql -u root -p
```
```sql
CREATE DATABASE lifebridge;
CREATE USER 'lifebridge'@'localhost' IDENTIFIED BY 'your_strong_password';
GRANT ALL PRIVILEGES ON lifebridge.* TO 'lifebridge'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### macOS
```bash
# Install Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install MySQL
brew install mysql
brew services start mysql

# Create database and user
mysql_secure_installation
mysql -u root -p
```
```sql
CREATE DATABASE lifebridge;
CREATE USER 'lifebridge'@'localhost' IDENTIFIED BY 'your_strong_password';
GRANT ALL PRIVILEGES ON lifebridge.* TO 'lifebridge'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### Windows
1. Download MySQL Installer from MySQL Website.
2. Complete installation.
3. Use MySQL Workbench to create the database:
```sql
CREATE DATABASE lifebridge;
CREATE USER 'lifebridge'@'localhost' IDENTIFIED BY 'your_strong_password';
GRANT ALL PRIVILEGES ON lifebridge.* TO 'lifebridge'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Java Setup

#### Ubuntu
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java --version
```

#### macOS
```bash
brew install openjdk@17
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java --version
```

#### Windows
1. Download OpenJDK 17.
2. Set `JAVA_HOME` environment variable.
3. Add to PATH.

### 3. Clone Repository
```bash
git clone https://github.com/your-organization/life-bridge-backend.git
cd life-bridge-backend
```

### 4. Configuration
Open `src/main/resources/application-dev.properties` and update database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lifebridge
spring.datasource.username=lifebridge
spring.datasource.password=your_strong_password
```

### 5. Build and Run

#### Development Mode
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Production Deployment
```bash
# Create executable JAR
mvn clean package -Pproduction

# Run the JAR
java -jar target/life-bridge-backend.jar
```

## 🔐 Initial Credentials
- **Admin**: `admin@example.com` / `password123`
- **Dean**: `dean@example.com` / `password123`
- **User**: `user@example.com` / `password123`

⚠️ **IMPORTANT**: Change these passwords immediately after first login!

## 🛠 Environment Variables
- `JWT_SECRET_KEY`: JWT token secret
- `JWT_EXPIRY_TIME`: Token expiration time (milliseconds)

## 📦 Dependencies
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL Connector
- Hibernate
- Lombok
- ModelMapper

## 🤝 Contributing
1. Fork the repository.
2. Create a feature branch.
3. Commit changes.
4. Push to branch.
5. Create a pull request.

## 📄 License
[Specify your license]

## 🆘 Support
For issues or questions, please open a GitHub issue.

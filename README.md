# KNUdev Backend

Welcome to the **KNUdev Backend** repository! 🎉 This is the backend service for the official website of the **KNUdev organization**.

## 🚀 Overview

The backend is a robust and scalable solution built to power the **Head Website for KNUdev**, providing key functionalities and services to support the organization.

> **Language Composition:**  
> - **Java**: 99.9%  
> - **Dockerfile**: 0.1%  

## 🛠️ Features

- 🔐 **Secure Authentication**: User login and management.
- 📄 **Content Management**: Manage dynamic content for the website.
- 📊 **API Integration**: Exposes RESTful APIs for frontend interaction.
- ⚙️ **Scalability**: Designed to handle large workloads efficiently.
- 📦 **Dockerized**: Easy setup and deployment with Docker.

## 🚀 Getting Started

Follow these steps to set up and run the project locally:

### Prerequisites

- [Java 17](https://adoptium.net/temurin/releases/?version=17)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/KNUdev/knu-dev_backend.git
   cd knu-dev_backend
   ```

2. **Build the project with Maven**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   java -jar target/knu-dev_backend.jar
   ```

4. **Run with Docker**:
   ```bash
   docker build -t knu-dev_backend .
   docker run -p 8080:8080 knu-dev_backend
   ```

## 📧 Contact

If you have any questions or need further assistance, feel free to reach out:

- **Maintainer**: DenysLnk
- **Email**: [ds.leonenko@gmail.com]

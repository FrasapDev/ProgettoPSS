# Recording API - User and Todo Management System

Welcome to the **Recording API** documentation! This is a FastAPI-based application that provides a simple yet powerful user and todo management system with full CRUD operations.

## 🚀 Features

- **RESTful API** built with FastAPI
- **CRUD Operations** for user and todo management (Create, Read, Update, Delete)
- **SQLite Database** with SQLAlchemy ORM
- **Automated Testing** with pytest
- **Continuous Integration/Deployment** with GitLab CI/CD
- **Docker Support** for easy deployment
- **Comprehensive Documentation** with MkDocs

## 📋 Overview

This application demonstrates a complete DevOps workflow, including:

- **Build Stage**: Dependency resolution and environment setup
- **Verify Stage**: Static analysis (Prospector) and security scanning (Bandit) and a dynamic verify
- **Test Stage**: Unit and integration testing with coverage reports
- **Package Stage**: Creating distributable Python packages
- **Release Stage**: Building and publishing Docker images
- **Docs Stage**: Generating and deploying documentation

## 🏗️ Architecture

The application follows a clean architecture pattern:

```
app/
├── api/            # API endpoints and routes
├── core/           # Core configuration and logging
├── db/             # Database models and schema
├── models/         # Pydantic models for validation
└── services/       # Business logic layer
```

## 🔗 Quick Links

- [Installation Guide](getting-started/installation.md)
- [Quick Start Tutorial](getting-started/quickstart.md)

## 📊 API Endpoints

The application provides the following RESTful endpoints:

### User Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users` | List all users |
| POST | `/api/v1/users` | Create a new user |
| GET | `/api/v1/users/{id}` | Get a specific user |
| PUT | `/api/v1/users/{id}` | Update a user |
| DELETE | `/api/v1/users/{id}` | Delete a user |

### Todo Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/todos` | List all todos |
| POST | `/api/v1/todos` | Create a new todo |
| GET | `/api/v1/todos/{id}` | Get a specific todo |
| PUT | `/api/v1/todos/{id}` | Update a todo |
| DELETE | `/api/v1/todos/{id}` | Delete a todo |
| PATCH | `/api/v1/todos/{id}/toggle` | Toggle completion status |

## 🧪 Testing

The project includes comprehensive tests covering:

- API endpoint functionality
- Database operations
- Service layer logic
- Error handling

Run tests with:

```bash
pytest tests/ -v --cov=app
```

## 🐳 Docker Deployment

The application is containerized and can be deployed using Docker:

```bash
docker build -t recording-api .
docker run -p 8000:80 recording-api
```

## 👥 Team Members

- Francesco Saponara
- Cristina M. Stanculea
- Mattia Chittoni

## 📝 Repository

**GitLab Repository:** [2025_assignment2_python_pipeline_group32](https://gitlab.com/f.saponara/2025_assignment2_python_pipeline_group32)

---

*Built with ❤️ using FastAPI, SQLAlchemy, and GitLab CI/CD*
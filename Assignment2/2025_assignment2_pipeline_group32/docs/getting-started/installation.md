# Installation Guide

This guide will help you set up the Recording API project on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Python 3.13** or higher
- **pip** (Python package manager)
- **Git** (for cloning the repository)
- **Docker** (optional, for containerized deployment)

## Installation Steps

### 1. Clone the Repository

```bash
git clone https://gitlab.com/f.saponara/2025_assignment2_python_pipeline_group32
cd 2025_assignment2_python_pipeline_group32
```

### 2. Create a Virtual Environment

It's recommended to use a virtual environment to isolate project dependencies:

```bash
python -m venv .venv
```

Activate the virtual environment:

- **Linux/Mac:**
  ```bash
  source .venv/bin/activate
  ```

- **Windows:**
  ```bash
  .venv\Scripts\activate
  ```

### 3. Install Dependencies

Install all required dependencies using pip:

```bash
pip install -r requirements.txt
```

Or if you're using `uv` (faster alternative):

```bash
uv sync
```

### 4. Configure Environment Variables

Create a `.env` file in the project root:

```bash
# Database Configuration
DB_NAME=test.db
DB_USER=admin
DB_PASSWORD=secret

# Application Configuration
DEBUG=False
```

### 5. Initialize the Database

The database will be automatically created when you first run the application. SQLite is used by default.

### 6. Verify Installation

Run the application to verify everything is set up correctly:

```bash
uvicorn app.main:app --reload
```

The API should now be running at `http://localhost:8000`

## Docker Installation

Alternatively, you can run the application using Docker:

### Build the Docker Image

```bash
docker build -t recording-api .
```

### Run the Container

```bash
docker run -p 8000:80 recording-api
```

Or use Docker Compose:

```bash
docker-compose up
```

## Verifying the Installation

Once the application is running, you can verify it by:

1. **Opening the interactive API documentation:**
   - Swagger UI: `http://localhost:8000/docs`
   - ReDoc: `http://localhost:8000/redoc`

2. **Making a test request:**
   ```bash
   curl http://localhost:8000/api/v1/users
   ```

## Troubleshooting

### Common Issues

**Issue: Python version mismatch**
```
Solution: Ensure you're using Python 3.13 or higher
python --version
```

**Issue: Dependencies fail to install**
```
Solution: Upgrade pip and try again
pip install --upgrade pip
pip install -r requirements.txt
```

**Issue: Port 8000 already in use**
```
Solution: Use a different port
uvicorn app.main:app --reload --port 8001
```
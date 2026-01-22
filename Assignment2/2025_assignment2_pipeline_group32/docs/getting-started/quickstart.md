# Quick Start Guide

Get up and running with the Recording API in just a few minutes!

## Prerequisites

Make sure you have completed the [Installation](installation.md) steps before proceeding.

## Starting the Server

### Using Uvicorn (Development)

```bash
# Activate your virtual environment
source .venv/bin/activate  # Linux/Mac
# or
.venv\Scripts\activate     # Windows

# Start the server with auto-reload
uvicorn app.main:app --reload
```

The API will be available at: `http://localhost:8000`

### Using Docker

```bash
# Build and run with Docker Compose
docker-compose up

# Or build and run manually
docker build -t recording-api .
docker run -p 8000:80 recording-api
```

## Your First API Request

### Create a User

Let's create your first user:

```bash
curl -X POST "http://localhost:8000/api/v1/users" \
     -H "Content-Type: application/json" \
     -d '{"name": "Alan Turing"}'
```

**Response:**
```json
{
  "id": 1,
  "name": "Alan Turing"
}
```

### List All Users

Retrieve all users in the system:

```bash
curl -X GET "http://localhost:8000/api/v1/users"
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Alan Turing"
  }
]
```

### Get a Specific User

Get details of a single user:

```bash
curl -X GET "http://localhost:8000/api/v1/users/1"
```

### Update a User

Update an existing user's information:

```bash
curl -X PUT "http://localhost:8000/api/v1/users/1" \
     -H "Content-Type: application/json" \
     -d '{"name": "Alan Mathison Turing"}'
```

### Delete a User

Remove a user from the system:

```bash
curl -X DELETE "http://localhost:8000/api/v1/users/1"
```

## Using the Interactive Documentation

FastAPI provides beautiful interactive documentation out of the box!

### Swagger UI

Navigate to: `http://localhost:8000/docs`

Here you can:
- 📋 View all available endpoints
- 🧪 Test endpoints directly in your browser
- 📖 See request/response schemas
- 💾 Download the OpenAPI specification

### ReDoc

Navigate to: `http://localhost:8000/redoc`

This provides an alternative documentation view with:
- Clean, organized layout
- Detailed schema information
- Easy navigation

## Example Workflow

Here's a typical workflow using the API:

### Step 1: Create Multiple Users

```bash
# Create first user
curl -X POST "http://localhost:8000/api/v1/users" \
     -H "Content-Type: application/json" \
     -d '{"name": "Ada Lovelace"}'

# Create second user
curl -X POST "http://localhost:8000/api/v1/users" \
     -H "Content-Type: application/json" \
     -d '{"name": "Grace Hopper"}'

# Create third user
curl -X POST "http://localhost:8000/api/v1/users" \
     -H "Content-Type: application/json" \
     -d '{"name": "Margaret Hamilton"}'
```

### Step 2: List All Users

```bash
curl -X GET "http://localhost:8000/api/v1/users"
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Ada Lovelace"
  },
  {
    "id": 2,
    "name": "Grace Hopper"
  },
  {
    "id": 3,
    "name": "Margaret Hamilton"
  }
]
```

### Step 3: Update a User

```bash
curl -X PUT "http://localhost:8000/api/v1/users/2" \
     -H "Content-Type: application/json" \
     -d '{"name": "Grace Murray Hopper"}'
```

### Step 4: Delete a User

```bash
curl -X DELETE "http://localhost:8000/api/v1/users/3"
```

## Using Python Requests

If you prefer Python, here's a complete example:

```python
import requests

# Base URL
BASE_URL = "http://localhost:8000/api/v1"

# Create a user
response = requests.post(
    f"{BASE_URL}/users",
    json={"name": "Dennis Ritchie"}
)
user = response.json()
user_id = user["id"]
print(f"Created user: {user}")

# Get all users
response = requests.get(f"{BASE_URL}/users")
users = response.json()
print(f"All users: {users}")

# Get specific user
response = requests.get(f"{BASE_URL}/users/{user_id}")
user = response.json()
print(f"User details: {user}")

# Update user
response = requests.put(
    f"{BASE_URL}/users/{user_id}",
    json={"name": "Dennis MacAlistair Ritchie"}
)
updated_user = response.json()
print(f"Updated user: {updated_user}")

# Delete user
response = requests.delete(f"{BASE_URL}/users/{user_id}")
result = response.json()
print(f"Deletion result: {result}")
```

## Running Tests

Verify everything is working correctly:

```bash
# Run all tests
pytest tests/ -v

# Run with coverage
pytest tests/ --cov=app --cov-report=html

# Open coverage report
open htmlcov/index.html  # Mac
xdg-open htmlcov/index.html  # Linux
start htmlcov/index.html  # Windows
```

## Viewing Logs

The application logs important events to the console:

```
INFO:     Started server process [12345]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
```

## Database Location

The SQLite database file is created in the project root:

```
test.db
```

To reset the database, simply delete this file and restart the server.

### Port Already in Use

If port 8000 is busy:

```bash
# Use a different port
uvicorn app.main:app --reload --port 8001
```

### Database Locked

If you see "database is locked" errors:

```bash
# Stop the server and restart
# Or delete test.db and restart
rm test.db
uvicorn app.main:app --reload
```

### Import Errors

If you see module import errors:

```bash
# Make sure you're in the virtual environment
source .venv/bin/activate

# Reinstall dependencies
pip install -r requirements.txt
```


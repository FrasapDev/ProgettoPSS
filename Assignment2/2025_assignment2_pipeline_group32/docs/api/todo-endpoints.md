# Todo Endpoints

This page documents all available todo management endpoints in the Recording API.

## Base URL

```
http://localhost:8000/api/v1
```

## Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/todos` | Retrieve all todos |
| POST | `/todos` | Create a new todo |
| GET | `/todos/{todo_id}` | Retrieve a specific todo |
| PUT | `/todos/{todo_id}` | Update a todo |
| DELETE | `/todos/{todo_id}` | Delete a todo |
| PATCH | `/todos/{todo_id}/toggle` | Toggle completion status |

---

## Get All Todos

Retrieve a list of all todos, optionally filtered by completion status.

**Endpoint:** `GET /api/v1/todos`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `completed` | boolean | No | Filter by completion status (true/false) |

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "title": "Buy groceries",
    "description": "Milk, bread, and eggs",
    "completed": false
  },
  {
    "id": 2,
    "title": "Finish homework",
    "description": null,
    "completed": true
  }
]
```

**cURL Examples:**

```bash
# Get all todos
curl -X GET "http://localhost:8000/api/v1/todos"

# Get only completed todos
curl -X GET "http://localhost:8000/api/v1/todos?completed=true"

# Get only incomplete todos
curl -X GET "http://localhost:8000/api/v1/todos?completed=false"
```

---

## Create a New Todo

Create a new todo item.

**Endpoint:** `POST /api/v1/todos`

**Request Body:**

```json
{
  "title": "Buy groceries",
  "description": "Milk, bread, and eggs",
  "completed": false
}
```

**Field Descriptions:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `title` | string | Yes | Todo title (1-200 chars) |
| `description` | string | No | Todo description (max 1000 chars) |
| `completed` | boolean | No | Completion status (default: false) |

**Response:** `201 Created`

```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, bread, and eggs",
  "completed": false
}
```

**cURL Example:**

```bash
curl -X POST "http://localhost:8000/api/v1/todos" \
     -H "Content-Type: application/json" \
     -d '{
       "title": "Buy groceries",
       "description": "Milk, bread, and eggs",
       "completed": false
     }'
```

**Python Example:**

```python
import requests

data = {
    "title": "Buy groceries",
    "description": "Milk, bread, and eggs",
    "completed": False
}

response = requests.post("http://localhost:8000/api/v1/todos", json=data)
todo = response.json()
print(f"Created todo with ID: {todo['id']}")
```

**Validation Errors:**

- `422 Unprocessable Entity`: Invalid request body

```json
{
  "detail": [
    {
      "loc": ["body", "title"],
      "msg": "field required",
      "type": "value_error.missing"
    }
  ]
}
```

---

## Get Todo by ID

Retrieve details of a specific todo.

**Endpoint:** `GET /api/v1/todos/{todo_id}`

**Path Parameters:**

- `todo_id` (integer): The ID of the todo to retrieve

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, bread, and eggs",
  "completed": false
}
```

**cURL Example:**

```bash
curl -X GET "http://localhost:8000/api/v1/todos/1"
```

**Error Responses:**

- `404 Not Found`: Todo does not exist

```json
{
  "detail": "Todo not found"
}
```

---

## Update Todo

Update an existing todo's information. All fields are optional.

**Endpoint:** `PUT /api/v1/todos/{todo_id}`

**Path Parameters:**

- `todo_id` (integer): The ID of the todo to update

**Request Body:**

```json
{
  "title": "Buy groceries and cook dinner",
  "description": "Updated shopping list",
  "completed": true
}
```

**Partial Update Example:**

```json
{
  "completed": true
}
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "Buy groceries and cook dinner",
  "description": "Updated shopping list",
  "completed": true
}
```

**cURL Examples:**

```bash
# Full update
curl -X PUT "http://localhost:8000/api/v1/todos/1" \
     -H "Content-Type: application/json" \
     -d '{
       "title": "Buy groceries and cook dinner",
       "description": "Updated shopping list",
       "completed": true
     }'

# Partial update (only mark as completed)
curl -X PUT "http://localhost:8000/api/v1/todos/1" \
     -H "Content-Type: application/json" \
     -d '{"completed": true}'
```

**Error Responses:**

- `404 Not Found`: Todo does not exist
- `422 Unprocessable Entity`: Invalid request body

---

## Delete Todo

Delete a todo from the system.

**Endpoint:** `DELETE /api/v1/todos/{todo_id}`

**Path Parameters:**

- `todo_id` (integer): The ID of the todo to delete

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Todo deleted successfully"
}
```

**cURL Example:**

```bash
curl -X DELETE "http://localhost:8000/api/v1/todos/1"
```

**Error Responses:**

- `404 Not Found`: Todo does not exist

```json
{
  "detail": "Todo not found"
}
```

---

## Toggle Todo Completion

Toggle the completion status of a todo (completed ↔ incomplete).

**Endpoint:** `PATCH /api/v1/todos/{todo_id}/toggle`

**Path Parameters:**

- `todo_id` (integer): The ID of the todo to toggle

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, bread, and eggs",
  "completed": true
}
```

**cURL Example:**

```bash
curl -X PATCH "http://localhost:8000/api/v1/todos/1/toggle"
```

**Python Example:**

```python
import requests

todo_id = 1
response = requests.patch(f"http://localhost:8000/api/v1/todos/{todo_id}/toggle")
updated_todo = response.json()
print(f"Todo is now {'completed' if updated_todo['completed'] else 'incomplete'}")
```

**Error Responses:**

- `404 Not Found`: Todo does not exist

---

## Data Models

### TodoCreate

Used when creating a new todo.

```python
{
  "title": string,        # Required, 1-200 characters
  "description": string,  # Optional, max 1000 characters
  "completed": boolean    # Optional, default: false
}
```

### TodoUpdate

Used when updating a todo. All fields are optional.

```python
{
  "title": string,        # Optional, 1-200 characters
  "description": string,  # Optional, max 1000 characters
  "completed": boolean    # Optional
}
```

### TodoRead

Returned when retrieving todo information.

```python
{
  "id": integer,          # Auto-generated todo ID
  "title": string,        # Todo title
  "description": string,  # Todo description (nullable)
  "completed": boolean    # Completion status
}
```

---

## Common Use Cases

### Create a Simple Todo

```bash
curl -X POST "http://localhost:8000/api/v1/todos" \
     -H "Content-Type: application/json" \
     -d '{"title": "Call dentist"}'
```

### Mark Todo as Complete

```bash
# Option 1: Using update
curl -X PUT "http://localhost:8000/api/v1/todos/1" \
     -H "Content-Type: application/json" \
     -d '{"completed": true}'

# Option 2: Using toggle
curl -X PATCH "http://localhost:8000/api/v1/todos/1/toggle"
```

### Get All Incomplete Todos

```bash
curl -X GET "http://localhost:8000/api/v1/todos?completed=false"
```

### Complete Workflow Example

```python
import requests

BASE_URL = "http://localhost:8000/api/v1/todos"

# 1. Create a todo
response = requests.post(BASE_URL, json={"title": "Learn FastAPI"})
todo_id = response.json()["id"]

# 2. Get the todo
todo = requests.get(f"{BASE_URL}/{todo_id}").json()
print(f"Created: {todo}")

# 3. Update with description
updated = requests.put(
    f"{BASE_URL}/{todo_id}",
    json={"description": "Complete the tutorial"}
).json()
print(f"Updated: {updated}")

# 4. Mark as complete
completed = requests.patch(f"{BASE_URL}/{todo_id}/toggle").json()
print(f"Completed: {completed}")

# 5. Delete when done
requests.delete(f"{BASE_URL}/{todo_id}")
print("Deleted!")
```

---

## Error Handling

All endpoints follow standard HTTP status codes:

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created (POST requests) |
| 404 | Resource not found |
| 422 | Validation error |
| 500 | Internal server error |

---

## Next Steps

- Learn about [User endpoints](user-endpoints.md)
- Read the [Quick Start guide](../getting-started/quickstart.md)
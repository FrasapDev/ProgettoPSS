# User Endpoints

This page documents all available user management endpoints in the Recording API.

## Base URL

```
http://localhost:8000/api/v1
```

## Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | Retrieve all users |
| POST | `/users` | Create a new user |
| GET | `/users/{user_id}` | Retrieve a specific user |
| PUT | `/users/{user_id}` | Update a user |
| DELETE | `/users/{user_id}` | Delete a user |

---

## Get All Users

Retrieve a list of all users in the system.

**Endpoint:** `GET /api/v1/users`

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Ada Lovelace"
  },
  {
    "id": 2,
    "name": "Grace Hopper"
  }
]
```

**cURL Example:**

```bash
curl -X GET "http://localhost:8000/api/v1/users"
```

**Python Example:**

```python
import requests

response = requests.get("http://localhost:8000/api/v1/users")
users = response.json()
print(users)
```

---

## Create a New User

Create a new user in the system.

**Endpoint:** `POST /api/v1/users`

**Request Body:**

```json
{
  "name": "Ada Lovelace"
}
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Ada Lovelace"
}
```

**cURL Example:**

```bash
curl -X POST "http://localhost:8000/api/v1/users" \
     -H "Content-Type: application/json" \
     -d '{"name": "Ada Lovelace"}'
```

**Python Example:**

```python
import requests

data = {"name": "Ada Lovelace"}
response = requests.post("http://localhost:8000/api/v1/users", json=data)
user = response.json()
print(f"Created user with ID: {user['id']}")
```

**Validation:**

- `name` (required): String, minimum 1 character

**Error Responses:**

- `422 Unprocessable Entity`: Invalid request body

```json
{
  "detail": [
    {
      "loc": ["body", "name"],
      "msg": "field required",
      "type": "value_error.missing"
    }
  ]
}
```

---

## Get User by ID

Retrieve details of a specific user.

**Endpoint:** `GET /api/v1/users/{user_id}`

**Path Parameters:**

- `user_id` (integer): The ID of the user to retrieve

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Ada Lovelace"
}
```

**cURL Example:**

```bash
curl -X GET "http://localhost:8000/api/v1/users/1"
```

**Python Example:**

```python
import requests

user_id = 1
response = requests.get(f"http://localhost:8000/api/v1/users/{user_id}")
user = response.json()
print(user)
```

**Error Responses:**

- `404 Not Found`: User does not exist

```json
{
  "detail": "User not found"
}
```

---

## Update User

Update an existing user's information.

**Endpoint:** `PUT /api/v1/users/{user_id}`

**Path Parameters:**

- `user_id` (integer): The ID of the user to update

**Request Body:**

```json
{
  "name": "Grace Hopper"
}
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Grace Hopper"
}
```

**cURL Example:**

```bash
curl -X PUT "http://localhost:8000/api/v1/users/1" \
     -H "Content-Type: application/json" \
     -d '{"name": "Grace Hopper"}'
```

**Python Example:**

```python
import requests

user_id = 1
data = {"name": "Grace Hopper"}
response = requests.put(f"http://localhost:8000/api/v1/users/{user_id}", json=data)
updated_user = response.json()
print(updated_user)
```

**Error Responses:**

- `404 Not Found`: User does not exist
- `422 Unprocessable Entity`: Invalid request body

---

## Delete User

Delete a user from the system.

**Endpoint:** `DELETE /api/v1/users/{user_id}`

**Path Parameters:**

- `user_id` (integer): The ID of the user to delete

**Response:** `200 OK`

```json
{
  "success": true
}
```

**cURL Example:**

```bash
curl -X DELETE "http://localhost:8000/api/v1/users/1"
```

**Python Example:**

```python
import requests

user_id = 1
response = requests.delete(f"http://localhost:8000/api/v1/users/{user_id}")
result = response.json()
if result["success"]:
    print(f"User {user_id} deleted successfully")
```

**Error Responses:**

- `404 Not Found`: User does not exist

```json
{
  "detail": "User not found"
}
```

---

## Data Models

### UserCreate

Used when creating or updating a user.

```python
{
  "name": string  # Required, user's name
}
```

### UserRead

Returned when retrieving user information.

```python
{
  "id": integer,    # Auto-generated user ID
  "name": string    # User's name
}
```

---

## Error Handling

All endpoints follow standard HTTP status codes:

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 404 | Resource not found |
| 422 | Validation error |
| 500 | Internal server error |

Error responses include a `detail` field with information about the error:

```json
{
  "detail": "Error message here"
}
```

---

## Testing with FastAPI Docs

FastAPI provides interactive API documentation at:

- **Swagger UI:** http://localhost:8000/docs
- **ReDoc:** http://localhost:8000/redoc

These interfaces allow you to:
- View all endpoints
- See request/response schemas
- Test endpoints directly in the browser
- Download OpenAPI specification

---

## Rate Limiting

Currently, there are no rate limits implemented. This may be added in future versions.

## Authentication

Currently, the API does not require authentication. This is suitable for the educational purpose of this project but should be implemented for production use.

---

"""
Tests for Todo API endpoints.
Tests all CRUD operations and edge cases for the Todo entity.
"""

import pytest
from fastapi.testclient import TestClient

from app.main import app
from tests.test_db import override_get_db, test_engine, TestingSessionLocal
from app.db.schema import Base, get_db, User, Todo


# Override the get_db dependency
app.dependency_overrides[get_db] = override_get_db

client = TestClient(app)


@pytest.fixture(autouse=True)
def setup_database():
    # Ensure tables exist and start each test with a clean dataset. We avoid
    # dropping the whole schema because other test modules share the same
    # in-memory engine; instead delete rows from the tables between tests.
    Base.metadata.create_all(bind=test_engine)
    # Clear existing rows
    db = TestingSessionLocal()
    try:
        # Delete from child tables first if there are FKs (not strictly
        # necessary here but good practice).
        db.query(Todo).delete()
        db.query(User).delete()
        db.commit()
    finally:
        db.close()

    yield


class TestTodoEndpoints:
    def test_create_todo(self):
        """Test creating a new todo."""
        response = client.post(
            "/api/v1/todos",
            json={
                "title": "Buy groceries",
                "description": "Milk, bread, and eggs",
                "completed": False
            }
        )
        assert response.status_code == 201
        data = response.json()
        assert data["title"] == "Buy groceries"
        assert data["description"] == "Milk, bread, and eggs"
        assert data["completed"] is False
        assert "id" in data

    def test_create_todo_minimal(self):
        """Test creating a todo with only required fields."""
        response = client.post(
            "/api/v1/todos",
            json={"title": "Simple task"}
        )
        assert response.status_code == 201
        data = response.json()
        assert data["title"] == "Simple task"
        assert data["description"] is None
        assert data["completed"] is False

    def test_create_todo_invalid_empty_title(self):
        """Test creating a todo with empty title fails."""
        response = client.post(
            "/api/v1/todos",
            json={"title": ""}
        )
        assert response.status_code == 422

    def test_create_todo_missing_title(self):
        """Test creating a todo without title fails."""
        response = client.post(
            "/api/v1/todos",
            json={"description": "No title"}
        )
        assert response.status_code == 422

    def test_list_todos_empty(self):
        """Test listing todos when none exist."""
        response = client.get("/api/v1/todos")
        assert response.status_code == 200
        assert response.json() == []

    def test_list_todos(self):
        """Test listing all todos."""
        # Create multiple todos
        client.post("/api/v1/todos", json={"title": "Task 1", "completed": False})
        client.post("/api/v1/todos", json={"title": "Task 2", "completed": True})
        client.post("/api/v1/todos", json={"title": "Task 3", "completed": False})

        response = client.get("/api/v1/todos")
        assert response.status_code == 200
        data = response.json()
        assert len(data) == 3
        assert data[0]["title"] == "Task 1"
        assert data[1]["title"] == "Task 2"
        assert data[2]["title"] == "Task 3"

    def test_list_todos_filter_completed(self):
        """Test filtering todos by completion status."""
        # Create todos with different statuses
        client.post("/api/v1/todos", json={"title": "Done task", "completed": True})
        client.post("/api/v1/todos", json={"title": "Pending task", "completed": False})
        client.post("/api/v1/todos", json={"title": "Another done", "completed": True})

        # Filter completed todos
        response = client.get("/api/v1/todos?completed=true")
        assert response.status_code == 200
        data = response.json()
        assert len(data) == 2
        assert all(todo["completed"] is True for todo in data)

        # Filter incomplete todos
        response = client.get("/api/v1/todos?completed=false")
        assert response.status_code == 200
        data = response.json()
        assert len(data) == 1
        assert data[0]["completed"] is False

    def test_get_todo(self):
        """Test retrieving a specific todo by ID."""
        # Create a todo
        create_response = client.post(
            "/api/v1/todos",
            json={"title": "Test todo", "description": "Description"}
        )
        todo_id = create_response.json()["id"]

        # Get the todo
        response = client.get(f"/api/v1/todos/{todo_id}")
        assert response.status_code == 200
        data = response.json()
        assert data["id"] == todo_id
        assert data["title"] == "Test todo"
        assert data["description"] == "Description"

    def test_get_todo_not_found(self):
        """Test retrieving a non-existent todo returns 404."""
        response = client.get("/api/v1/todos/999")
        assert response.status_code == 404
        assert response.json()["detail"] == "Todo not found"

    def test_update_todo_full(self):
        """Test updating all fields of a todo."""
        # Create a todo
        create_response = client.post(
            "/api/v1/todos",
            json={"title": "Original", "description": "Original desc", "completed": False}
        )
        todo_id = create_response.json()["id"]

        # Update the todo
        response = client.put(
            f"/api/v1/todos/{todo_id}",
            json={
                "title": "Updated title",
                "description": "Updated description",
                "completed": True
            }
        )
        assert response.status_code == 200
        data = response.json()
        assert data["title"] == "Updated title"
        assert data["description"] == "Updated description"
        assert data["completed"] is True

    def test_update_todo_partial(self):
        """Test partial update of a todo."""
        # Create a todo
        create_response = client.post(
            "/api/v1/todos",
            json={"title": "Original", "completed": False}
        )
        todo_id = create_response.json()["id"]

        # Update only the title
        response = client.put(
            f"/api/v1/todos/{todo_id}",
            json={"title": "New title"}
        )
        assert response.status_code == 200
        data = response.json()
        assert data["title"] == "New title"
        assert data["completed"] is False  # Should remain unchanged

    def test_update_todo_not_found(self):
        """Test updating a non-existent todo returns 404."""
        response = client.put(
            "/api/v1/todos/999",
            json={"title": "Updated"}
        )
        assert response.status_code == 404

    def test_delete_todo(self):
        """Test deleting a todo."""
        # Create a todo
        create_response = client.post(
            "/api/v1/todos",
            json={"title": "To be deleted"}
        )
        todo_id = create_response.json()["id"]

        # Delete the todo
        response = client.delete(f"/api/v1/todos/{todo_id}")
        assert response.status_code == 200
        assert response.json()["success"] is True

        # Verify it's deleted
        get_response = client.get(f"/api/v1/todos/{todo_id}")
        assert get_response.status_code == 404

    def test_delete_todo_not_found(self):
        """Test deleting a non-existent todo returns 404."""
        response = client.delete("/api/v1/todos/999")
        assert response.status_code == 404

    def test_toggle_todo_completion(self):
        """Test toggling todo completion status."""
        # Create an incomplete todo
        create_response = client.post(
            "/api/v1/todos",
            json={"title": "Toggle test", "completed": False}
        )
        todo_id = create_response.json()["id"]

        # Toggle to completed
        response = client.patch(f"/api/v1/todos/{todo_id}/toggle")
        assert response.status_code == 200
        assert response.json()["completed"] is True

        # Toggle back to incomplete
        response = client.patch(f"/api/v1/todos/{todo_id}/toggle")
        assert response.status_code == 200
        assert response.json()["completed"] is False

    def test_toggle_todo_not_found(self):
        """Test toggling a non-existent todo returns 404."""
        response = client.patch("/api/v1/todos/999/toggle")
        assert response.status_code == 404

    def test_todo_crud_workflow(self):
        """Test complete CRUD workflow for todos."""
        # 1. Create
        create_response = client.post(
            "/api/v1/todos",
            json={"title": "Workflow test", "completed": False}
        )
        assert create_response.status_code == 201
        todo_id = create_response.json()["id"]

        # 2. Read
        get_response = client.get(f"/api/v1/todos/{todo_id}")
        assert get_response.status_code == 200
        assert get_response.json()["title"] == "Workflow test"

        # 3. Update
        update_response = client.put(
            f"/api/v1/todos/{todo_id}",
            json={"title": "Updated workflow", "completed": True}
        )
        assert update_response.status_code == 200
        assert update_response.json()["completed"] is True

        # 4. Delete
        delete_response = client.delete(f"/api/v1/todos/{todo_id}")
        assert delete_response.status_code == 200

        # 5. Verify deletion
        final_get = client.get(f"/api/v1/todos/{todo_id}")
        assert final_get.status_code == 404
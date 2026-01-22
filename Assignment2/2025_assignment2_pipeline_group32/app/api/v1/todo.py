from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session

from app.db.schema import get_db
from app.models.todo import TodoCreate, TodoRead, TodoUpdate
from app.services.todo_service import TodoService


router = APIRouter(prefix="/todos", tags=["todos"])


def get_todo_service(db: Session = Depends(get_db)) -> TodoService:
    return TodoService(db)


@router.get("", response_model=List[TodoRead], summary="List all todos")
def list_todos(
    completed: Optional[bool] = Query(None, description="Filter by completion status"),
    service: TodoService = Depends(get_todo_service)
):
    return service.list_todos(completed=completed)


@router.post("", response_model=TodoRead, status_code=201, summary="Create a new todo")
def create_todo(
    todo: TodoCreate,
    service: TodoService = Depends(get_todo_service)
):
    return service.create_todo(todo)


@router.get("/{todo_id}", response_model=TodoRead, summary="Get a specific todo")
def get_todo(
    todo_id: int,
    service: TodoService = Depends(get_todo_service)
):
    todo = service.get_todo(todo_id)
    
    if not todo:
        raise HTTPException(status_code=404, detail="Todo not found")
    
    return todo


@router.put("/{todo_id}", response_model=TodoRead, summary="Update a todo")
def update_todo(
    todo_id: int,
    todo_data: TodoUpdate,
    service: TodoService = Depends(get_todo_service)
):
    todo = service.update_todo(todo_id, todo_data)
    
    if not todo:
        raise HTTPException(status_code=404, detail="Todo not found")
    
    return todo


@router.delete("/{todo_id}", summary="Delete a todo")
def delete_todo(
    todo_id: int,
    service: TodoService = Depends(get_todo_service)
):
    success = service.delete_todo(todo_id)
    
    if not success:
        raise HTTPException(status_code=404, detail="Todo not found")
    
    return {"success": True, "message": "Todo deleted successfully"}


@router.patch("/{todo_id}/toggle", response_model=TodoRead, summary="Toggle todo completion")
def toggle_todo(
    todo_id: int,
    service: TodoService = Depends(get_todo_service)
):
    todo = service.toggle_completed(todo_id)
    
    if not todo:
        raise HTTPException(status_code=404, detail="Todo not found")
    
    return todo
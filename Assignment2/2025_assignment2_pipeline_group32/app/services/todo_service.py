from typing import List, Optional
from sqlalchemy.orm import Session

from app.db.schema import Todo
from app.models.todo import TodoCreate, TodoUpdate


class TodoService:
    def __init__(self, db: Session):
        self._db = db

    def list_todos(self, completed: Optional[bool] = None) -> List[Todo]:
        query = self._db.query(Todo)
        
        if completed is not None:
            query = query.filter(Todo.completed == completed)
            
        return query.all()

    def get_todo(self, todo_id: int) -> Optional[Todo]:
        return self._db.query(Todo).filter(Todo.id == todo_id).first()

    def create_todo(self, todo_data: TodoCreate) -> Todo:
        todo = Todo(
            title=todo_data.title,
            description=todo_data.description,
            completed=todo_data.completed
        )
        self._db.add(todo)
        self._db.commit()
        self._db.refresh(todo)
        return todo

    def update_todo(self, todo_id: int, todo_data: TodoUpdate) -> Optional[Todo]:
        todo = self.get_todo(todo_id)
        
        if not todo:
            return None

        # Update only provided fields
        update_data = todo_data.model_dump(exclude_unset=True)
        for field, value in update_data.items():
            setattr(todo, field, value)

        self._db.commit()
        self._db.refresh(todo)
        return todo

    def delete_todo(self, todo_id: int) -> bool:
        todo = self.get_todo(todo_id)
        
        if not todo:
            return False

        self._db.delete(todo)
        self._db.commit()
        return True

    def toggle_completed(self, todo_id: int) -> Optional[Todo]:
        todo = self.get_todo(todo_id)
        
        if not todo:
            return None

        todo.completed = not todo.completed
        self._db.commit()
        self._db.refresh(todo)
        return todo
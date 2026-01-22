"""
Main FastAPI application entry point.
Configures the application, routes, and initializes the database.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import importlib

# Import API modules explicitly to ensure their routers are loaded.
user = importlib.import_module("app.api.v1.user")
todo = importlib.import_module("app.api.v1.todo")
from app.core.logging import setup_logging
from app.db.schema import init_db


# Setup logging
setup_logging()

# Create FastAPI application
app = FastAPI(
    title="Recording API",
    description="FastAPI User and Todo Management System with CRUD Operations",
    version="0.1.0",
    docs_url="/docs",
    redoc_url="/redoc",
)

# Configure CORS (if needed for web frontends)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure appropriately for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize database tables
init_db()

# Include API routers
app.include_router(user.router, prefix="/api/v1")
app.include_router(todo.router, prefix="/api/v1")


@app.get("/", tags=["root"])
def read_root():
    return {
        "message": "Welcome to Recording API",
        "version": "0.1.0",
        "docs": "/docs",
        "redoc": "/redoc",
        "endpoints": {
            "users": "/api/v1/users",
            "todos": "/api/v1/todos"
        }
    }


@app.get("/health", tags=["health"])
def health_check():
    return {"status": "healthy"}
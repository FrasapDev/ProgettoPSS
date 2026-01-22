# tests/test_db.py

from sqlalchemy import create_engine, StaticPool
from sqlalchemy.orm import sessionmaker

# IMPORT MODELS + BASE + get_db
from app.db.schema import Base, get_db, User, Todo

# Create in-memory SQLite engine for testing
test_engine = create_engine(
    "sqlite:///:memory:",
    connect_args={"check_same_thread": False},
    poolclass=StaticPool,
)

TestingSessionLocal = sessionmaker(
    autocommit=False, autoflush=False, bind=test_engine
)

# Create all tables for testing
Base.metadata.create_all(bind=test_engine)


def override_get_db():
    """Override dependency to use test DB session."""
    db = TestingSessionLocal()
    try:
        yield db
    finally:
        db.close()

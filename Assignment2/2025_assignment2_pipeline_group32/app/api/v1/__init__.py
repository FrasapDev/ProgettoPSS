"""
API v1 module.
This module contains all version 1 API endpoints.
"""

# Import routers to make them available when importing this module
from . import user, todo  # noqa: F401

__all__ = ["user", "todo"]
from fastapi import FastAPI, HTTPException, status, Depends, APIRouter
from typing import List, Optional
import models
import schemas
import database
import hashing # type: ignore
from hashing import Hash # type: ignore
from database import engine, SessionLocal
from sqlalchemy.orm import Session
#import authentication
import oauth2
import uuid
from uuid import UUID


router = APIRouter(
    prefix="/categories",
    tags=['Categories']
)

@router.post('/create', status_code=status.HTTP_201_CREATED, response_model = models.Category)
async def create_category(info:schemas.CategoryCreate, db:Session = Depends(database.get_database)):
    create_data = info.model_dump(exclude_unset=True)

    new_category = models.Category(
        name = create_data['name'] if create_data['name'] else 'null'
    )
    db.add(new_category)
    db.commit()
    db.refresh(new_category)
from fastapi import FastAPI, HTTPException, status, Depends, APIRouter
from typing import List, Optional
import models as models
import schemas as schemas
import database as database
import hashing as hashing # type: ignore
from hashing import Hash # type: ignore
from database import engine, SessionLocal
from sqlalchemy.orm import Session
#import authentication
import oauth2 as oauth2
import uuid
from uuid import UUID


router = APIRouter(
    prefix="/categories",
    tags=['Categories']
)


@router.get('/', status_code=status.HTTP_200_OK, response_model=List[schemas.Category])
async def get_categs(db:Session = Depends(database.get_database)) -> List[schemas.Category]:
    categs:List[models.Category] = db.query(models.Category).all()
    if not categs:
        raise HTTPException(detail="No category found!", status_code=status.HTTP_404_NOT_FOUND)

    return categs

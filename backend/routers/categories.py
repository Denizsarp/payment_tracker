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

@router.get('/{category_id}', status_code=status.HTTP_200_OK, response_model=schemas.Category)
async def get_category(category_id:UUID, db:Session = Depends(database.get_database)) -> schemas.Category:
    target_category = db.query(models.Category).filter(models.Category.id == category_id).first()
    if not target_category:
        raise HTTPException(detail="no category found!", status_code=status.HTTP_404_NOT_FOUND)

    else:
        return target_category
    


#category creation endpoint

@router.post('/create-category', status_code=status.HTTP_200_OK, response_model=schemas.Category)
async def create_category(new_features:schemas.CategoryCreate, db:Session = Depends(database.get_database)) ->schemas.Category:
    create_data = new_features.model_dump(exclude_unset=True)

    new_category:models.Category = models.Category(
        name = create_data['name']
    )
    db.add(new_category)
    db.commit()
    db.refresh(new_category)

    return new_category



#category deletion endpoint

@router.delete('/{categ_name}', status_code=status.HTTP_200_OK, response_model=str)
async def delete_category(category_name:str, db:Session = Depends(database.get_database)) -> str:
    target_category = db.query(models.Category).filter(models.Category.name == category_name).first()
    if not target_category:
        raise HTTPException(detail="No category name found!", status_code=status.HTTP_404_NOT_FOUND)

    db.delete(target_category)
    db.commit()
    return 'removal is successful!'



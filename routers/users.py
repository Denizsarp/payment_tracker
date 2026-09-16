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
    prefix="/users",
    tags=['User']
)


#GET ALL USERS
@router.get('/', status_code=status.HTTP_200_OK, response_model=List[schemas.UserDisplay])
async def get_all_users(db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)) -> List[schemas.UserDisplay]:
    all_users = db.query(models.User).all()
    return all_users


#GET YOURSELF PROFILE
@router.get('/me', status_code=status.HTTP_200_OK, response_model=schemas.UserDisplay)
async def my_profile(db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)) -> schemas.UserDisplay:
    target_user:models.User = db.query(models.User).filter(models.User.id == current_user.id).first()

    if not target_user:
        raise HTTPException(detail="User not found!", status_code=status.HTTP_404_NOT_FOUND)

    return target_user




#DELETE YOUR PROFILE
@router.delete('/me', status_code=status.HTTP_200_OK, response_model=str)
async def delete_my_profile(db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)) -> str:
    target_user:models.User = db.query(models.User).filter(models.User.id == current_user.id).first()
    if not target_user:
        raise HTTPException(detail="User not found!", status_code=status.HTTP_404_NOT_FOUND)

    all_subscriptions = target_user.subscriptions
    for subs in all_subscriptions:
        db.delete(subs)

    db.flush()
    db.delete(target_user)
    
    db.commit()

    return 'removal is done!'






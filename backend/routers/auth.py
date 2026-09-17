from fastapi import FastAPI, HTTPException, status, Depends, APIRouter
from fastapi.security import OAuth2PasswordRequestForm
from typing import List, Optional
import backend.models as models
import backend.schemas as schemas
import backend.database as database
import backend.hashing as hashing # type: ignore
from backend.hashing import Hash # type: ignore
from backend.database import engine, SessionLocal
from sqlalchemy.orm import Session
#import authentication
import backend.oauth2 as oauth2
import uuid
from uuid import UUID
import backend.jwtToken as jwtToken
from backend.jwtToken import TokenOP
import secrets



router = APIRouter(
    prefix="/auth",
    tags=['Auth']
)



@router.post('/register', status_code=status.HTTP_201_CREATED, response_model=schemas.User)
async def register(user_info:schemas.UserCreate, db:Session = Depends(database.get_database)) -> schemas.User:
    create_data = user_info.model_dump(exclude_unset=True)

    current_email = create_data['email'].strip().lower()
    current_username = create_data['username'].strip()

    if db.query(models.User).filter(models.User.username == current_username).first():
        raise HTTPException(detail="Username already registered!", status_code=status.HTTP_409_CONFLICT)

    if db.query(models.User).filter(models.User.email == current_email).first():
        raise HTTPException(detail="E-mail already registered!", status_code=status.HTTP_409_CONFLICT)

    hashed_password = Hash.bcrypt(create_data['password'])

    curren_verification_code = (secrets.randbelow(900000) + 100000) 

    new_user:models.User = models.User(
        username = current_username,
        email = current_email,
        password = hashed_password,
        target_spending = create_data['target_spending']
    )
    db.add(new_user)
    db.commit()
    db.refresh()




@router.post('/login', status_code=status.HTTP_200_OK, response_model=dict)
async def user_login(request:OAuth2PasswordRequestForm, db:Session = Depends(database.get_database)) -> dict:
    user_info:str = request.username

    if '@' in user_info:
        user = db.query(models.User).filter(models.User.email == user_info).first()
    else:
        user = db.query(models.User).filter(models.User.username == user_info).first()

    if not user:
        raise HTTPException(detail='Invalid Credidentals!', status_code=status.HTTP_401_UNAUTHORIZED)

    verify_password = Hash.verify_password(request.password, user.password)

    if not verify_password:
        raise HTTPException(detail="Email verification not confirmed!", status_code=status.HTTP_403_FORBIDDEN)

    else:
        access_token = TokenOP.create_access_token(
            data={
                "sub" : str(user.id)
            }
        )


    return {
        "access_token" : access_token,
        "token_type" : "Bearer"
    }




@router.delete('/delete', status_code=status.HTTP_200_OK, response_model=str)
async def user_delete(db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)) -> str:
    if not db.query(models.User).filter(models.User.id == current_user.id).first():
        raise HTTPException(detail="User not found!", status_code=status.HTTP_404_NOT_FOUND)

    subs:List[models.Subscription] = current_user.subscriptions

    for sub in subs:
        db.delete(sub)

    db.delete(current_user)
    db.flush()
    db.commit()

    return 'removal is done!'

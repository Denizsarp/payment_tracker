from fastapi import FastAPI, HTTPException, status, Depends, APIRouter
from typing import List, Optional
import models
import schemas
import database
import hashing
from hashing import Hash
from datetime import datetime
from database import engine, SessionLocal
from sqlalchemy.orm import Session
#import authentication
import oauth2
import uuid
from uuid import UUID



router = APIRouter(
    prefix="/subscriptions",
    tags=['Subscriptions']
)


#GET ALL SUBSCRIPTIONS(TEST-ONLY)
@router.get('/', status_code=status.HTTP_200_OK, response_model=List[schemas.Subscription])
async def get_all_subs(db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)):
    all_subs = db.query(models.Subscription).all()
    if len(all_subs) == 0:
        raise HTTPException(detail="no subscription found!", status_code=status.HTTP_404_NOT_FOUND)
    
    return all_subs


#GET MY SUBSCRIPTIONS
@router.get('/my-subscriptions', status_code=status.HTTP_200_OK, response_model=List[schemas.SubscriptionCreate])
async def get_my_subs(db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)):
    user_subs = current_user.subscriptions
    if len(user_subs) == 0:
        raise HTTPException(detail="no subscription found!", status_code=status.HTTP_404_NOT_FOUND)
    return user_subs


@router.post('/create', status_code=status.HTTP_201_CREATED, response_model=schemas.SubscriptionCreate)
async def create_subs(new_features:schemas.SubscriptionCreate, db:Session = Depends(database.get_database), current_user:models.User = Depends(oauth2.get_current_user)) -> schemas.SubscriptionCreate:
    create_data = new_features.model_dump(exclude_unset=True)

    new_subs = models.Subscription(
        name = create_data['name'],
        amount = create_data['amount'],
        pay_cycle = create_data['pay_cycle'],
        next_payment_date = create_data['next_payment_date']
    )

    db.add(new_subs)
    db.commit()
    db.refresh(new_subs)

    return new_subs
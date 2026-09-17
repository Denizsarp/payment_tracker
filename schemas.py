from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List
from uuid import UUID


#--------------------USER INPUT-------------------------

class UserCreate(BaseModel):
    username:str
    email:str
    password:str
    target_spending:float


class UserUpdate(BaseModel):
    username:Optional[str] = None
    email:Optional[str] = None
    password:Optional[str] = None
    target_spending = Optional[float] = None

#----------------------------------------------------------

class CategoryCreate(BaseModel):
    name:str




#-------------------------GENERAL CLASSES-------------------

class Category(BaseModel):
    id:UUID
    name:str


class Subscription(BaseModel):
    id:UUID
    name:str
    amount:float
    pay_cycle:str
    next_payment_date:datetime
    category:Category

    model_config = {
        "from_attributes" : True
    } 


class User(BaseModel):
    id : UUID
    username:str
    email:str
    password:str
    total_monthly_spending:float
    Subscriptions : List[Subscription] = []

    model_config = {
        "from_attributes" : True
    }

#------------------------------------------------------

class SubscriptionCreate(BaseModel):
    name:str
    amount:float #Numeric
    pay_cycle:str
    next_payment_date:datetime
    category:Category



class UserDisplay(BaseModel):
    username:str
    subscriptions : List[Subscription] = []
    total_monthly_spending:float

    model_config = {
        "from_attributes": True
    }







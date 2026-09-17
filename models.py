import sqlalchemy
from sqlalchemy import Integer, String, Boolean, Column, DateTime, ForeignKey, Text, Float, Numeric
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.orm import relationship
from datetime import datetime
import uuid
from database import Base



class User(Base):
    __tablename__ = "users"

    id = Column(UUID(as_uuid=True), primary_key=True, nullable=False, default=uuid.uuid4)
    username = Column(String, nullable=False)
    email = Column(String, nullable=False)
    password = Column(String, nullable=False)
    target_spending = Column(Float, nullable=False)
    total_monthly_spending = Column(Float, default= 0.0, nullable=True)


    subscriptions = relationship(
        "Subscription",
        back_populates="user",
        cascade="all, delete-orphan"
    )



class Subscription(Base):
    __tablename__ = "subscriptions"

    id = Column(UUID(as_uuid=True), primary_key=True, nullable=False, default=uuid.uuid4)
    name = Column(String, nullable=False)
    amount = Column(Numeric(10, 2), nullable=False)
    pay_cycle = Column(String, nullable=False, default="month")
    next_payment_date = Column(DateTime, nullable=False)


    user_id = Column(
        UUID(as_uuid=True),
        ForeignKey("users.id"),
        nullable=False
    )

    category_id = Column(
        UUID(as_uuid=True),
        ForeignKey("categories.id")
    )


class Category(Base):
    __tablename__ = "categories"

    id = Column(UUID(as_uuid=True), primary_key=True, nullable=False, default=uuid.uuid4)
    category_name = Column(String, nullable=False)


    
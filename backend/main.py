from fastapi import FastAPI, status
import models
import schemas
import database
import hashing
from database import engine, SessionLocal, Base
from routers import auth, users, subscriptions


app = FastAPI(
    title="Payment Tracker API",
    version="1.0.0"
)


Base.metadata.create_all(bind=engine)

app.include_router(auth.router)
app.include_router(users.router)
app.include_router(subscriptions.router)



@app.get('/', status_code=status.HTTP_200_OK, response_model=dict)
async def status_check() -> dict:
    return {
        'message' : "Payment tracker server is up! welcome a board captain",
        'status' : "OK"
    }
from datetime import datetime, timedelta, timezone
from jose import JWTError, jwt
from dotenv import load_dotenv
import os


load_dotenv()


SECRET_KEY = os.getenv("SECRET_KEY")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60


class TokenOP():
    @staticmethod
    def create_access_token(data:dict, expires_delta:timedelta | None = None):
        to_encode = data.copy()
        if expires_delta:
            expiration_date = datetime.now(timezone.utc) + expires_delta
        else:
            expiration_date = datetime.now(timezone.utc) + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)

        to_encode.update({
            "exp" : expiration_date
        })

        encoded_jwt = jwt.encode(
            to_encode,
            SECRET_KEY,
            algorithm=ALGORITHM
        )

        return encoded_jwt


    @staticmethod
    def verify_token(token:str, credentials_exception):

        try:
            payload = jwt.decode(
                token,
                SECRET_KEY,
                algorithms=[ALGORITHM]
            )

            user_id = payload.get('sub')
            if user_id is None:
                raise credentials_exception
            return user_id
        
        except JWTError:
            raise credentials_exception

    
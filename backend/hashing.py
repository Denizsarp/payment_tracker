from passlib.context import CryptContext


pwd_context = CryptContext(
    schemes=["bcrypt"],
    deprecated="auto"
)



class Hash:
    @staticmethod
    def bcrypt(plain_password:str) -> str:
        hashed_password:str = pwd_context.hash(plain_password)
        return hashed_password

    @staticmethod
    def verify_password(plain_password:str, hashed_password:str):
        return pwd_context.verify(plain_password, hashed_password)
        
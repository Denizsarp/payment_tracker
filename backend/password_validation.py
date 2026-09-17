class PasswordOP():
    @staticmethod
    def validate_password(plain_password:str) -> str:
        if len(plain_password) < 8 or len(plain_password) > 25:
            exc = 'Password must be longer than 8 characters and shorter than 25 characters!'
        elif' ' in plain_password:
            exc = 'Password can not contain space character!'
        elif plain_password.isdigit():
            exc = 'Password must include at least 1 letter'
        else:
            exc = ''

        return exc

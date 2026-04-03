#!/usr/bin/env python3
"""테스트 계정 JWT 토큰 재발급 스크립트"""
import hmac, hashlib, base64, json, time, os

SECRET = os.getenv("JWT_SECRET", "dev-secret")

def b64url(data):
    if isinstance(data, str): data = data.encode()
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode()

def make_jwt(user_id, expiry_days=30):
    header  = b64url(json.dumps({"alg": "HS256", "typ": "JWT"}, separators=(",", ":")))
    exp     = int(time.time()) + expiry_days * 86400
    payload = b64url(json.dumps({"iss": "lifemash-backend", "userId": user_id, "exp": exp}, separators=(",", ":")))
    msg     = f"{header}.{payload}".encode()
    sig     = hmac.new(SECRET.encode(), msg, hashlib.sha256).digest()
    return f"{header}.{payload}.{b64url(sig)}"

ACCOUNTS = [
    ("00000000-0000-0000-0000-000000000001", "alpha"),
    ("00000000-0000-0000-0000-000000000002", "beta"),
    ("00000000-0000-0000-0000-000000000003", "gamma"),
]

for uid, name in ACCOUNTS:
    print(f"{name}: {make_jwt(uid)}")

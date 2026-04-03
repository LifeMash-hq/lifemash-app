# 테스트 계정

JWT secret: `dev-secret` (기본값, `.env`에 `JWT_SECRET` 미설정 시)  
만료: 2025-05-03 (30일)

재발급 필요 시:
```bash
cd core/ktor/seed && python3 gen_tokens.py
```

---

## alpha
- **ID**: `00000000-0000-0000-0000-000000000001`
- **email**: test.alpha@lifemash.dev
- **provider**: KAKAO / test-kakao-001
- **nickname**: alpha
- **token**:
  ```
  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJsaWZlbWFzaC1iYWNrZW5kIiwidXNlcklkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAxIiwiZXhwIjoxNzc3ODIwMDcxfQ.QWzlQqAmNT9UiSxkj7LURlART96od5lAw4omOG0LLrg
  ```

## beta
- **ID**: `00000000-0000-0000-0000-000000000002`
- **email**: test.beta@lifemash.dev
- **provider**: GOOGLE / test-google-001
- **nickname**: beta
- **token**:
  ```
  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJsaWZlbWFzaC1iYWNrZW5kIiwidXNlcklkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAyIiwiZXhwIjoxNzc3ODIwMDcxfQ.fQ_uR3yKDE76YFGMeC2Oc0T7WpolrEt-InIahppCTC4
  ```

## gamma
- **ID**: `00000000-0000-0000-0000-000000000003`
- **email**: test.gamma@lifemash.dev
- **provider**: KAKAO / test-kakao-002
- **nickname**: gamma
- **token**:
  ```
  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJsaWZlbWFzaC1iYWNrZW5kIiwidXNlcklkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAzIiwiZXhwIjoxNzc3ODIwMDcxfQ.A-rqTMAW9Aty1dad0HjHXDNhHpzH2XEXTMnDNNgHW5Y
  ```

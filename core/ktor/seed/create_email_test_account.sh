#!/bin/bash
# 이메일 테스트 계정 생성 스크립트
# 서버가 실행 중일 때 실행: bash seed/create_email_test_account.sh

BASE_URL="${1:-http://localhost:8080}"

echo "이메일 테스트 계정 생성 중... ($BASE_URL)"

RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/auth/email" \
  -H "Content-Type: application/json" \
  -d '{"email":"test.delta@lifemash.dev","password":"Test1234!"}')

echo "응답: $RESPONSE"

ACCESS_TOKEN=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('accessToken',''))" 2>/dev/null)

if [ -n "$ACCESS_TOKEN" ]; then
  echo ""
  echo "✓ delta 계정 생성 완료"
  echo "  email:    test.delta@lifemash.dev"
  echo "  password: Test1234!"
  echo "  token:    $ACCESS_TOKEN"
else
  echo "✗ 생성 실패 (이미 존재하거나 서버 오류)"
fi

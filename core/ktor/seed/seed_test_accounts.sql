-- 테스트 계정 seed
-- 실행: psql "postgresql://localhost:5432/lifemash?user=yibeomseok" -f seed/seed_test_accounts.sql
-- 이메일 계정(delta)은 서버 기동 후 create_email_test_account.sh 으로 별도 생성

INSERT INTO users (id, email, provider, provider_id, nickname, profile_image, bio, created_at, updated_at)
VALUES
  ('00000000-0000-0000-0000-000000000001', 'test.alpha@lifemash.dev', 'KAKAO',  'test-kakao-001',  'alpha', NULL, '테스트 계정 alpha', NOW(), NOW()),
  ('00000000-0000-0000-0000-000000000002', 'test.beta@lifemash.dev',  'GOOGLE', 'test-google-001', 'beta',  NULL, '테스트 계정 beta',  NOW(), NOW()),
  ('00000000-0000-0000-0000-000000000003', 'test.gamma@lifemash.dev', 'KAKAO',  'test-kakao-002',  'gamma', NULL, '테스트 계정 gamma', NOW(), NOW())
ON CONFLICT DO NOTHING;

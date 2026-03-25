package org.bmsk.lifemash.assistant

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 사용자 API 키 암호화/복호화 유틸리티.
 *
 * 사용자가 자신의 Claude API 키를 등록할 때,
 * 평문(plainText)으로 DB에 저장하면 DB가 유출될 때 위험하므로
 * AES-GCM 알고리즘으로 암호화하여 저장한다.
 *
 * AES-GCM:
 * - AES: 대칭키 암호화 (같은 키로 암호화/복호화)
 * - GCM: 인증 태그 포함 → 데이터 위변조 방지
 * - IV(Initialization Vector): 매번 랜덤 생성하여 같은 평문도 다른 암호문이 되도록 함
 *
 * 저장 형식: Base64(IV + 암호화된데이터)
 */
object ApiKeyEncryption {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128  // 인증 태그 비트 길이
    private const val IV_LENGTH = 12        // IV 바이트 길이

    // 환경변수에서 암호화 비밀키를 가져옴 (Base64 인코딩된 AES 키)
    private val secretKey: SecretKeySpec by lazy {
        val keyBase64 = org.bmsk.lifemash.config.EnvConfig.require("API_KEY_ENCRYPTION_SECRET")
        SecretKeySpec(Base64.getDecoder().decode(keyBase64), "AES")
    }

    /** 평문 → 암호문(Base64 문자열)으로 암호화 */
    fun encrypt(plainText: String): String {
        // 매번 새로운 랜덤 IV 생성 → 같은 키를 암호화해도 매번 다른 결과
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        }
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted  // IV를 암호문 앞에 붙여서 저장 (복호화 시 필요)
        return Base64.getEncoder().encodeToString(combined)
    }

    /** 암호문(Base64) → 평문으로 복호화 */
    fun decrypt(cipherText: String): String {
        val combined = Base64.getDecoder().decode(cipherText)
        val iv = combined.copyOfRange(0, IV_LENGTH)              // 앞 12바이트 = IV
        val encrypted = combined.copyOfRange(IV_LENGTH, combined.size)  // 나머지 = 암호화된 데이터
        val cipher = Cipher.getInstance(ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        }
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }
}

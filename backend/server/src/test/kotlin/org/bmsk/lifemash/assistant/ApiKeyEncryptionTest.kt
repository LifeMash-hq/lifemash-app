package org.bmsk.lifemash.assistant

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiKeyEncryptionTest {

    @Test
    fun `암호화 후 복호화하면 원본과 동일하다`() {
        // Given
        val originalKey = "sk-ant-test-key-12345"

        // When
        val encrypted = ApiKeyEncryption.encrypt(originalKey)
        val decrypted = ApiKeyEncryption.decrypt(encrypted)

        // Then
        assertEquals(originalKey, decrypted)
    }

    @Test
    fun `같은 평문이라도 매번 다른 암호문이 생성된다`() {
        // Given
        val key = "sk-ant-test-key-12345"

        // When
        val encrypted1 = ApiKeyEncryption.encrypt(key)
        val encrypted2 = ApiKeyEncryption.encrypt(key)

        // Then
        assert(encrypted1 != encrypted2) { "같은 평문에 대해 동일한 암호문이 생성되었다" }
        assertEquals(key, ApiKeyEncryption.decrypt(encrypted1))
        assertEquals(key, ApiKeyEncryption.decrypt(encrypted2))
    }

    @Test
    fun `빈 문자열을 암호화하고 복호화한다`() {
        // Given
        val empty = ""

        // When
        val encrypted = ApiKeyEncryption.encrypt(empty)
        val decrypted = ApiKeyEncryption.decrypt(encrypted)

        // Then
        assertEquals(empty, decrypted)
    }

    @Test
    fun `특수 문자가 포함된 키를 암호화하고 복호화한다`() {
        // Given
        val specialKey = "sk-ant_key!@#\$%^&*()_+-=한글テスト"

        // When
        val encrypted = ApiKeyEncryption.encrypt(specialKey)
        val decrypted = ApiKeyEncryption.decrypt(encrypted)

        // Then
        assertEquals(specialKey, decrypted)
    }

    @Test
    fun `손상된 암호문은 복호화에 실패한다`() {
        // Given
        val corrupted = "dGhpcyBpcyBub3QgYSB2YWxpZCBjaXBoZXJ0ZXh0"

        // When & Then
        assertFailsWith<Exception> {
            ApiKeyEncryption.decrypt(corrupted)
        }
    }
}

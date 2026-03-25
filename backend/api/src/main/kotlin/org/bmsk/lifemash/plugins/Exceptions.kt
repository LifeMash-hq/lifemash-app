package org.bmsk.lifemash.plugins

// ── 커스텀 예외 클래스들 ──
// 코드 어디서든 이 예외를 throw하면 StatusPages에서 자동으로 처리됨
class BadRequestException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)
class ForbiddenException(message: String = "Forbidden") : RuntimeException(message)
class NotFoundException(message: String = "Not found") : RuntimeException(message)
class ConflictException(message: String = "Conflict") : RuntimeException(message)

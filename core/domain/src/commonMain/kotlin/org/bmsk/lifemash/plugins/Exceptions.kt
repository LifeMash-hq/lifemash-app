package org.bmsk.lifemash.plugins

class BadRequestException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)
class ForbiddenException(message: String = "Forbidden") : RuntimeException(message)
class NotFoundException(message: String = "Not found") : RuntimeException(message)
class ConflictException(message: String = "Conflict") : RuntimeException(message)

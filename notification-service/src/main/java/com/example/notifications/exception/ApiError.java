package com.example.notifications.exception;

import java.time.OffsetDateTime;

public record ApiError(OffsetDateTime timestamp, int status, String code, String message) {
}

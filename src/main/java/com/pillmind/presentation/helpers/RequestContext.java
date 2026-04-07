package com.pillmind.presentation.helpers;

import java.util.UUID;

import io.javalin.http.Context;

/**
 * Utilitário para rastreamento básico de requisições.
 */
public final class RequestContext {
    public static final String REQUEST_ID_ATTRIBUTE = "request.id";
    public static final String REQUEST_START_TIME_ATTRIBUTE = "request.start-time-ms";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    private RequestContext() {
    }

    public static String initialize(Context ctx) {
        String requestId = ctx.header(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        ctx.attribute(REQUEST_ID_ATTRIBUTE, requestId);
        ctx.attribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());
        ctx.header(REQUEST_ID_HEADER, requestId);
        return requestId;
    }

    public static String getRequestId(Context ctx) {
        String requestId = ctx.attribute(REQUEST_ID_ATTRIBUTE);
        if (requestId == null || requestId.isBlank()) {
            return initialize(ctx);
        }
        return requestId;
    }

    public static long getDurationInMs(Context ctx) {
        Long startTime = ctx.attribute(REQUEST_START_TIME_ATTRIBUTE);
        if (startTime == null) {
            return -1L;
        }
        return System.currentTimeMillis() - startTime;
    }
}

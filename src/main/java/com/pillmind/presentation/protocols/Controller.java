package com.pillmind.presentation.protocols;

import io.javalin.http.Context;

/**
 * Interface base para controllers
 */
@FunctionalInterface
public interface Controller {
    void handle(Context ctx);
}

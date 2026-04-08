package com.pillmind.main.routes;

import io.javalin.Javalin;

/**
 * Interface base para configuração de rotas
 */
public interface Routes {
    void setup(Javalin app);
}

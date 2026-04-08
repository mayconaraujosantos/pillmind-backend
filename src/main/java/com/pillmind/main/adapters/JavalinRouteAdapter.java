package com.pillmind.main.adapters;

import com.pillmind.presentation.protocols.Controller;

import io.javalin.Javalin;

/**
 * Adaptador para configurar rotas no Javalin
 */
public class JavalinRouteAdapter {
  private final Javalin app;

  public JavalinRouteAdapter(Javalin app) {
    this.app = app;
  }

  public void route(String method, String path, Controller controller) {
    switch (method.toUpperCase()) {
      case "GET" -> app.get(path, controller::handle);
      case "POST" -> app.post(path, controller::handle);
      case "PUT" -> app.put(path, controller::handle);
      case "PATCH" -> app.patch(path, controller::handle);
      case "DELETE" -> app.delete(path, controller::handle);
      default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }
  }

  public void get(String path, Controller controller) {
    app.get(path, controller::handle);
  }

  public void post(String path, Controller controller) {
    app.post(path, controller::handle);
  }

  public void put(String path, Controller controller) {
    app.put(path, controller::handle);
  }

  public void patch(String path, Controller controller) {
    app.patch(path, controller::handle);
  }

  public void delete(String path, Controller controller) {
    app.delete(path, controller::handle);
  }
}

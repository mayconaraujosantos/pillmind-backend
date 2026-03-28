package com.pillmind.main.routes;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.storage.ObjectStorageService;

import io.javalin.Javalin;

/**
 * GET público para servir ficheiros do MinIO na porta da API. Útil quando a firewall ou a rede
 * não expõem a porta 9000 ao telemóvel — define {@code MINIO_PUBLIC_BASE_URL=http://HOST:8080/api/media}.
 */
public class MediaRoutes implements Routes {
  private static final Logger logger = LoggerFactory.getLogger(MediaRoutes.class);

  private static final Pattern USER_ID = Pattern.compile(
      "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
  private static final Pattern PROFILE_FILE = Pattern.compile(
      "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\.(jpg|jpeg|png|webp)$");

  private final ObjectStorageService objectStorage;

  public MediaRoutes(ObjectStorageService objectStorage) {
    this.objectStorage = objectStorage;
  }

  @Override
  public void setup(Javalin app) {
    app.get("/api/media/profiles/{userId}/{filename}", ctx -> {
      String userId = ctx.pathParam("userId");
      String filename = ctx.pathParam("filename");
      if (!USER_ID.matcher(userId).matches() || !PROFILE_FILE.matcher(filename).matches()) {
        ctx.status(400);
        return;
      }
      String objectKey = ObjectStorageService.PROFILE_PREFIX + userId + "/" + filename;
      var opt = objectStorage.openPublicObject(objectKey);
      if (opt.isEmpty()) {
        ctx.status(404);
        return;
      }
      try (var obj = opt.get()) {
        ctx.contentType(obj.contentType());
        ctx.header("Cache-Control", "public, max-age=86400");
        if (obj.sizeBytes() > 0) {
          ctx.header("Content-Length", String.valueOf(obj.sizeBytes()));
        }
        obj.inputStream().transferTo(ctx.outputStream());
      } catch (Exception e) {
        logger.warn("Streaming media failed: {}", e.getMessage());
        if (!ctx.res().isCommitted()) {
          ctx.status(500);
        }
      }
    });

    app.get("/api/media/medicines/{userId}/{filename}", ctx -> {
      String userId = ctx.pathParam("userId");
      String filename = ctx.pathParam("filename");
      logger.info("Medicine media request: userId={}, filename={}", userId, filename);
      
      if (!USER_ID.matcher(userId).matches() || !PROFILE_FILE.matcher(filename).matches()) {
        logger.warn("Invalid paths: userId={}, filename={}", userId, filename);
        ctx.status(400);
        return;
      }
      String objectKey = ObjectStorageService.MEDICINE_PREFIX + userId + "/" + filename;
      logger.info("Looking for object key: {}", objectKey);
      
      var opt = objectStorage.openPublicObject(objectKey);
      if (opt.isEmpty()) {
        logger.warn("Object not found: {}", objectKey);
        ctx.status(404);
        return;
      }
      try (var obj = opt.get()) {
        logger.info("Found object: contentType={}, size={}", obj.contentType(), obj.sizeBytes());
        ctx.contentType(obj.contentType());
        ctx.header("Cache-Control", "public, max-age=86400");
        if (obj.sizeBytes() > 0) {
          ctx.header("Content-Length", String.valueOf(obj.sizeBytes()));
        }
        obj.inputStream().transferTo(ctx.outputStream());
        logger.info("Successfully served object: {}", objectKey);
      } catch (Exception e) {
        logger.warn("Streaming medicine media failed: {}", e.getMessage());
        if (!ctx.res().isCommitted()) {
          ctx.status(500);
        }
      }
    });
  }
}

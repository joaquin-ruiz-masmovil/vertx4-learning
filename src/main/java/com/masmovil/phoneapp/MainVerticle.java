package com.masmovil.phoneapp;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  private static final String CONFIGURATION_FILE_PATH = "src/main/conf/config.yaml";

  private static final String PHONES_PATH = "/v1/phones";
  private static final String COSTUMER_ORDER_PATH = "/v1/order";

  @Override
  public Completable rxStart() {

    LOGGER.info("Starting RESTful Verticles deploy...");

    return loadConfig()
      .flatMap(this::createServer)
      .ignoreElement();
  }

  private Single<JsonObject> loadConfig() {

    LOGGER.info("Loading configuration...");

    return ConfigRetriever.create(vertx,
      new ConfigRetrieverOptions()
        .setScanPeriod(2000)
        .addStore(
          new ConfigStoreOptions()
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", CONFIGURATION_FILE_PATH))
        )
    ).rxGetConfig();
  }

  private Single<?> createServer(JsonObject config) {

    HttpServerOptions serverOptions = new HttpServerOptions().setPort(config.getInteger("server.port"));

    return vertx.createHttpServer(serverOptions)
      .requestHandler(configureRouting())
      .rxListen()
      .doOnSuccess(server -> {
        LOGGER.info("Vertx RESTful Server launched on " + serverOptions.getPort());
      })
      .doOnError(throwable -> {
        LOGGER.error("Launch Vertx RESTful Server FAILED cause " + throwable.getMessage());
      });

  }

  private Router configureRouting() {

    Router router = Router.router(vertx);
    router.route().handler(LoggerHandler.create());

    router.get("/health").handler(ctx -> ctx.response().end(new JsonObject().put("status", "UP").toString()));

    router.route(HttpMethod.GET, PHONES_PATH).handler(System.out::println);
    LOGGER.info("Deploying Verticle: " + PHONES_PATH);
    //router.route(HttpMethod.POST, COSTUMER_ORDER_PATH).handler(costumerOrderRestHandler);
    //LOGGER.info("Deploying Verticle: {} {}", HttpMethod.POST, COSTUMER_ORDER_PATH);
    return router;

  }



}

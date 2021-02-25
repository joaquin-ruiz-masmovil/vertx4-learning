package com.masmovil.phoneapp;

import com.masmovil.phoneapp.infrastructure.routehandler.PhoneCatalogRestHandler;
import io.micronaut.context.BeanContext;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.reactivex.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.flywaydb.core.Flyway;

import java.util.Map;

public class RestfulVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestfulVerticle.class);

  private static final String CONFIGURATION_FILE_PATH = "src/main/resources/config.yaml";

  private static final String PHONES_PATH = "/v1/phones";
  private static final String COSTUMER_ORDER_PATH = "/v1/order";

  private BeanContext context;

  private PgPool pgPool;

  @Override
  public Completable rxStart() {

    LOGGER.info("Starting RESTful Verticle deploy...");

    return loadConfig()
      .flatMap(this::executeDBMigration)
      .flatMap(this::configureDBPool)
      .flatMap(this::loadIoC)
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


  private Single<JsonObject> executeDBMigration(JsonObject config) {

    Flyway flyway = Flyway.configure()
      .dataSource(config.getString("postgres.uri"),
        config.getString("postgres.user"),
        config.getString("postgres.pass"))
      .load();
    flyway.migrate();

    LOGGER.info("Flyway Migrating DB: " + config.getString("postgres.uri"));

    return Single.just(config);
  }

  private Single<JsonObject> configureDBPool(JsonObject config) {

    LOGGER.info("Configuring Pool DB");

    this.pgPool = PgPool.pool(Vertx.currentContext().owner(), new PgConnectOptions()
        .setDatabase(config.getString("postgres.db"))
        .setUser(config.getString("postgres.user"))
        .setPassword(config.getString("postgres.pass"))
        .setProperties(Map.of("search_path", config.getString("postgres.schema"))),
      new PoolOptions().setMaxSize(30));

    return Single.just(config);
  }

  private Single<JsonObject> loadIoC(JsonObject config) {

    LOGGER.info("Building IoC Context");

    context = BeanContext.run();
    context.registerSingleton(Pool.class, this.pgPool);

    return Single.just(config);
  }

  private Router configureRouting() {

    Router router = Router.router(vertx);
    // router.route().handler(LoggerHandler.create());

    router.get("/health").handler(ctx -> ctx.response().end(new JsonObject().put("status", "UP").toString()));

    router.route(HttpMethod.GET, PHONES_PATH).handler(context.getBean(PhoneCatalogRestHandler.class));
    LOGGER.info("Deploying Verticle: " + PHONES_PATH);
    //router.route(HttpMethod.POST, COSTUMER_ORDER_PATH).handler(costumerOrderRestHandler);
    //LOGGER.info("Deploying Verticle: {} {}", HttpMethod.POST, COSTUMER_ORDER_PATH);
    return router;

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


}

package com.masmovil.phoneapp.infrastructure.configuration;

import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import org.flywaydb.core.Flyway;

public class FlywayConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlywayConfiguration.class);

  JsonObject config;

  public FlywayConfiguration(JsonObject config) {
    this.config = config;
  }

  public Single<JsonObject> executeDBMigration() {

    Flyway flyway = Flyway.configure()
      .schemas(config.getString(ConfigurationProperties.DATABASE_SCHEMA))
      .dataSource(config.getString(ConfigurationProperties.DATABASE_URI),
        config.getString(ConfigurationProperties.DATABASE_USER),
        config.getString(ConfigurationProperties.DATABASE_PASS))
      .load();
    flyway.migrate();

    LOGGER.info("Flyway Migrating DB: " + config.getString(ConfigurationProperties.DATABASE_URI));

    return Single.just(config);

  }


}

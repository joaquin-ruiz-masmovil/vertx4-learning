package com.masmovil.phoneapp.infrastructure.routehandler;

import com.masmovil.phoneapp.application.getphonecatalog.GetPhoneCatalogService;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.http.HttpHeaders;
import io.vertx.reactivex.ext.web.RoutingContext;


public class PhoneCatalogRestHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PhoneCatalogRestHandler.class);

  private final GetPhoneCatalogService getPhoneCatalogService;

  public PhoneCatalogRestHandler(GetPhoneCatalogService getPhoneCatalogService) {
    this.getPhoneCatalogService = getPhoneCatalogService;
  }

  @Override
  public void handle(RoutingContext event) {

    LOGGER.info("Handler " + event.currentRoute().methods().toString() + " " + event.currentRoute().getPath());

    try {

      getPhoneCatalogService.execute()
        .subscribe(phonesList -> {
          LOGGER.info("Catalog retrieved successfully with " + phonesList.size() + " phones");
          event.response()
            .setChunked(true)
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(200)
            .end(Json.encodePrettily(phonesList));
        });

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      event.response().setStatusCode(500).end(e.getMessage());
    }

  }

}

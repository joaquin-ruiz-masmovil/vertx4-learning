package com.masmovil.phoneapp.infrastructure.routehandler;

import com.google.common.collect.Lists;
import com.masmovil.phoneapp.application.getphonecatalog.GetPhoneCatalogService;
import com.masmovil.phoneapp.application.getphonecatalog.PhoneCatalogInfo;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.junit5.VertxExtension;
import io.vertx.reactivex.core.http.HttpHeaders;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
public class PhoneCatalogRestHandlerShould {

  private static final String PHONES_PATH = "/v1/phones";

  private PhoneCatalogRestHandler phoneCatalogRestHandler;

  @Mock
  private RoutingContext event;

  @Mock
  private HttpServerResponse httpServerResponse;

  @Mock
  private GetPhoneCatalogService getPhoneCatalogServiceMock;

  @Mock
  Route route;

  @BeforeEach
  void setUp() {
    phoneCatalogRestHandler = new PhoneCatalogRestHandler(getPhoneCatalogServiceMock);
    when(event.currentRoute()).thenReturn(route);
    when(route.methods()).thenReturn(Set.of(HttpMethod.GET));
    when(route.getPath()).thenReturn(PHONES_PATH);
    when(event.response()).thenReturn(httpServerResponse);
    when(httpServerResponse.setStatusCode(any(Integer.class))).thenReturn(httpServerResponse);
  }

  @Test
  void return200WhenHandleRequestSuccessfully() {
    // given
    List<PhoneCatalogInfo> catalog = Lists.newArrayList(
      new PhoneCatalogInfo("Nokia", "3030", "http:://image.jpg", 100),
      new PhoneCatalogInfo("iPhone", "Version 12", "http:://image.png", 200)
    );
    when(getPhoneCatalogServiceMock.execute()).thenReturn(Single.just(catalog));
    when(httpServerResponse.setChunked(any(Boolean.class))).thenReturn(httpServerResponse);
    when(httpServerResponse.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON))
      .thenReturn(httpServerResponse);

    // when
    phoneCatalogRestHandler.handle(event);

    //then
    verify(httpServerResponse, times(1)).setStatusCode(OK.code());
    verify(httpServerResponse, times(0)).setStatusCode(INTERNAL_SERVER_ERROR.code());
    verify(httpServerResponse, times(1)).end(Json.encodePrettily(catalog));

  }

  @Test
  void return500WhenHandleRequestFails() {
    // given
    when(getPhoneCatalogServiceMock.execute()).thenThrow(new RuntimeException("Something fails!"));

    // when
    phoneCatalogRestHandler.handle(event);

    //then
    verify(httpServerResponse, times(0)).setStatusCode(OK.code());
    verify(httpServerResponse, times(1)).setStatusCode(INTERNAL_SERVER_ERROR.code());
    verify(httpServerResponse, times(1)).end("Something fails!");

  }

}

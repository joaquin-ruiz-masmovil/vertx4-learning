package com.masmovil.phoneapp.infrastructure.configuration;

import com.masmovil.phoneapp.application.getphonecatalog.GetPhoneCatalogService;
import com.masmovil.phoneapp.infrastructure.routehandler.PhoneCatalogRestHandler;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class RestfulVerticleConfiguration {

  @Singleton
  PhoneCatalogRestHandler phoneHandler(GetPhoneCatalogService getPhoneCatalogService) {
    return new PhoneCatalogRestHandler(getPhoneCatalogService);
  }

}

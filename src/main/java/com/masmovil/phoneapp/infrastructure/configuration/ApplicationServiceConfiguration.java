package com.masmovil.phoneapp.infrastructure.configuration;

import com.masmovil.phoneapp.application.getphonecatalog.GetPhoneCatalogService;
import com.masmovil.phoneapp.domain.repository.CatalogPhoneRepository;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class ApplicationServiceConfiguration {

  @Singleton
  public GetPhoneCatalogService getPhoneCatalogService(final CatalogPhoneRepository catalogPhoneRepository) {
    return new GetPhoneCatalogService(catalogPhoneRepository);
  }

}

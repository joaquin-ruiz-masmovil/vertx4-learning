package com.masmovil.phoneapp.infrastructure.configuration;

import com.masmovil.phoneapp.domain.repository.CatalogPhoneRepository;
import com.masmovil.phoneapp.infrastructure.repository.PostgresCatalogPhoneRepository;
import com.masmovil.phoneapp.infrastructure.repository.mapper.PostgresCatalogPhoneMapper;
import io.micronaut.context.annotation.Factory;
import io.vertx.reactivex.sqlclient.Pool;

import javax.inject.Singleton;

@Factory
public class RepositoryServiceConfiguration {

  @Singleton
  public CatalogPhoneRepository phoneRepository(Pool jdbcPool) {
    return new PostgresCatalogPhoneRepository(jdbcPool, new PostgresCatalogPhoneMapper());
  }

}

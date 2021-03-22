package com.masmovil.phoneapp.infrastructure.repository;


import com.masmovil.phoneapp.domain.model.CatalogPhone;
import com.masmovil.phoneapp.domain.repository.CatalogPhoneRepository;
import com.masmovil.phoneapp.infrastructure.repository.mapper.DomainMapper;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.reactivex.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PostgresCatalogPhoneRepository implements CatalogPhoneRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostgresCatalogPhoneRepository.class);

  private final Pool jdbcPool;
  private final DomainMapper<CatalogPhone> mapper;

  public PostgresCatalogPhoneRepository(Pool jdbcPool, DomainMapper<CatalogPhone> domainMapper) {
    this.jdbcPool = jdbcPool;
    this.mapper = domainMapper;
  }

  @Override
  public Single<List<CatalogPhone>> getPhoneCatalog() {

    LOGGER.info("Postgres DB get All Phone Catalog");

    return jdbcPool.preparedQuery("SELECT * FROM PHONES_CATALOGUE")
        .rxExecute()
        .flatMap(rows -> Observable.fromIterable(rows)
            .map(mapper::mapToDomain)
            .toList()
        );
  }

}

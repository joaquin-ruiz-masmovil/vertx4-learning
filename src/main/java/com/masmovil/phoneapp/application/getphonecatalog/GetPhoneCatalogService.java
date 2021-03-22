package com.masmovil.phoneapp.application.getphonecatalog;

import com.masmovil.phoneapp.domain.repository.CatalogPhoneRepository;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.List;

public class GetPhoneCatalogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPhoneCatalogService.class);

  private final CatalogPhoneRepository catalogPhoneRepository;

  public GetPhoneCatalogService(CatalogPhoneRepository catalogPhoneRepository) {
    this.catalogPhoneRepository = catalogPhoneRepository;
  }

  public Single<List<PhoneCatalogInfo>> execute() {

    LOGGER.info("Executing -> 'Get Phone Catalog Service'");

    return catalogPhoneRepository.getPhoneCatalog()
        .flattenAsObservable(phone -> phone)
        .map(phone -> new PhoneCatalogInfo(phone.getName(), phone.getDescription(), phone.getImage(), phone.getPrice()))
        .toList();
  }

}

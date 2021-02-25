package com.masmovil.phoneapp.infrastructure.repository.mapper;

import com.masmovil.phoneapp.domain.model.CatalogPhone;
import io.vertx.reactivex.sqlclient.Row;

public class PostgresCatalogPhoneMapper implements DomainMapper<CatalogPhone> {

  public CatalogPhone mapToDomain(Row row) {

    return CatalogPhone.of(row.getUUID("pc_id"),
        row.getString("pc_name"),
        row.getString("pc_description"),
        row.getString("pc_image"),
        row.getInteger("pc_price"));
  }

}

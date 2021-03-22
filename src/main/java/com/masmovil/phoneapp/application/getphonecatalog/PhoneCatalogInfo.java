package com.masmovil.phoneapp.application.getphonecatalog;

public class PhoneCatalogInfo {

  private final String name;
  private final String description;
  private final String image;
  private final Integer price;

  public PhoneCatalogInfo(String name, String description, String image, Integer price) {
    this.name = name;
    this.description = description;
    this.image = image;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }

  public Integer getPrice() {
    return price;
  }
}

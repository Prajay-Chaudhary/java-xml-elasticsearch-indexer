package com.adelean.assessment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentRecord {
  private String id;
  private String title;
  private String body;

  @JsonProperty("article_sku")
  private String articleSku;
  private String brand;
  @JsonProperty("brand_reference")
  private String brandReference;
  private String category;
  private String ean13;
  @JsonProperty("full_description")
  private String fullDescription;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getArticleSku() {
    return articleSku;
  }

  public void setArticleSku(String articleSku) {
    this.articleSku = articleSku;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getBrandReference() {
    return brandReference;
  }

  public void setBrandReference(String brandReference) {
    this.brandReference = brandReference;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getEan13() {
    return ean13;
  }

  public void setEan13(String ean13) {
    this.ean13 = ean13;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }
}

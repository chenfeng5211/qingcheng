package com.qingcheng.pojo.goods;


public class GoodRecyled {

  private String id;
  private String image;
  private String goodName;
  private Integer goodCategory;
  private long price;
  private long saleNum;
  private String spuId;

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


  public String getGoodName() {
    return goodName;
  }

  public void setGoodName(String goodName) {
    this.goodName = goodName;
  }


  public Integer getGoodCategory() {
    return goodCategory;
  }

  public void setGoodCategory(Integer goodCategory) {
    this.goodCategory = goodCategory;
  }


  public long getPrice() {
    return price;
  }

  public void setPrice(long price) {
    this.price = price;
  }


  public long getSaleNum() {
    return saleNum;
  }

  public void setSaleNum(long saleNum) {
    this.saleNum = saleNum;
  }

}

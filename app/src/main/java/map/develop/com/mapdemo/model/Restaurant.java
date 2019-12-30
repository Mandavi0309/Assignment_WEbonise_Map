package map.develop.com.mapdemo.model;

public class Restaurant {

  private String name;
  private String address;
  private String image;
  private String ratings;
  private String photoReference;

  public Restaurant(String name, String address, String image, String ratings) {
    this.name = name;
    this.address = address;
    this.image = image;
    this.ratings = ratings;
  }

  public String getPhotoReference() {
    return photoReference;
  }

  public void setPhotoReference(String photoReference) {
    this.photoReference = photoReference;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getRatings() {
    return ratings;
  }

  public void setRatings(String ratings) {
    this.ratings = ratings;
  }

  public Restaurant() {
    super();
  }

  @Override
  public String toString() {
    return "Restaurant{" +
        "name='" + name + '\'' +
        ", address='" + address + '\'' +
        ", image='" + image + '\'' +
        ", ratings='" + ratings + '\'' +
        ", photoReference='" + photoReference + '\'' +
        '}';
  }
}

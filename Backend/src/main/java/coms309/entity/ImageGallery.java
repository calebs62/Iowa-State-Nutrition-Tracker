package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="image_gallery")
public class ImageGallery {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="idimg")
    private int id;

    @Column(name="img", columnDefinition="LONGTEXT")
    private String img;

    public ImageGallery() {}

    public ImageGallery(String i) {
        img = i;
    }

    public int getId() {return id;}
    public String getImg() {return img;}
    public void setImg(String i) {img = i;}
}
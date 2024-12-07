package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name="image_gallery")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ImageGallery {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="idimg")
    private int id;


    @Column(name="img",
            columnDefinition="LONGTEXT")
    private String img;

    public ImageGallery(String i) {

        img = i;
    }

    public int getId() {return id;}

    public String getImg() {return img;}

    public void setImg(String i) {img = i;}


}

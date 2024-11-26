package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="image_gallery")
public class ImageGallery {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="idimg",
            columnDefinition="LONGTEXT")
    private int id;

    @ManyToOne
    @JoinColumn(name="activity_feed")
    private ActivityFeed activity;

    @Column(name="img")
    private String img;

    public ImageGallery(ActivityFeed a, String i) {
        activity = a;
        img = i;
    }

    public int getId() {return id;}
    public ActivityFeed getActivity() {return activity;};
    public String getImg() {return img;}

    public void setActivity(ActivityFeed a) {activity = a;}
    public void setImg(String i) {img = i;}


}

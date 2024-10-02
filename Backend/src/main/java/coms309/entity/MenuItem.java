package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="menu_item")

public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idmenuitem")
    private int id;

    @Column(name = "menu")
    private int menu;

    @Column(name = "fooditem")
    private int fooditem;

    public MenuItem() {}

    public MenuItem(int menu, int fooditem) {
        this.menu = menu;
        this.fooditem = fooditem;
    }
    public int getId() {return id;}
    public int getMenu() {return menu;}
    public int getFoodItem() {return fooditem;}
}

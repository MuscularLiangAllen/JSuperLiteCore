package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by Allen on 2018/1/30.
 */


@Entity
@Table(name = "T_MENU")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String url;

    private String description;

    private String params;

    private String icon;

    /**
     * "belongTo == -1" means this menu is a root menu item
     */
    @Column(name = "belong_to", nullable = false)
    private int belongTo;

    @Transient
    public static final int ROOM_MENU = -1;

    public Menu() {}

    public Menu(String name, String url, int belongTo, String icon) {
        this.name = name;
        this.url = url;
        this.belongTo = belongTo;
        this.icon = icon;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(int belongTo) {
        this.belongTo = belongTo;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return id == ((Menu) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

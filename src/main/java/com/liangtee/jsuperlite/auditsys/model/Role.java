package com.liangtee.jsuperlite.core.model;

import javax.persistence.*;

/**
 * Created by Allen on 2018/1/31.
 */


@Entity
@Table(name = "T_ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(name = "granted_menu_names")
    private String grantedMenuNames;

    @Column(name = "granted_menuids")
    private String grantedMenuIDs;

    @Column(nullable = false)
    private boolean editable;

    @Transient
    public static final String GRANTED_ALL = "granted_all";

    public Role() {}

    public Role(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGrantedMenuNames() {
        return grantedMenuNames;
    }

    public void setGrantedMenuNames(String grantedMenuNames) {
        this.grantedMenuNames = grantedMenuNames;
    }

    public String getGrantedMenuIDs() {
        return grantedMenuIDs;
    }

    public void setGrantedMenuIDs(String grantedMenuIDs) {
        this.grantedMenuIDs = grantedMenuIDs;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


}

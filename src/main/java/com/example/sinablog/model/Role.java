package com.example.sinablog.model;

import com.example.sinablog.model.enums.RoleName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Role extends BaseEntity{

    private RoleName name;


    @Enumerated(EnumType.STRING)
    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}

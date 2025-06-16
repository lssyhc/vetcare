package com.lima.vetcare.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "owners")
@DiscriminatorValue("OWNER")
public class Owner extends User {

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();

    public Owner() {
        super();
    }

    public Owner(String email, String password, String name) {
        super(email, password, name);
    }

    @Override
    public String getUserType() {
        return "OWNER";
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", name='" + getName() + '\'' +
                ", petsCount=" + pets.size() +
                '}';
    }
}

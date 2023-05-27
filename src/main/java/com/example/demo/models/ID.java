package com.example.demo.models;

import java.util.ArrayList;
import java.util.Optional;

public abstract class ID {
    public String id;
    public ID(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Optional<ID> findInListById(ArrayList<ID> list, String objId) {
        return list.stream().filter(listObj -> listObj.id.equals(objId)).findFirst();
    }
}

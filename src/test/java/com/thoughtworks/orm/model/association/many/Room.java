package com.thoughtworks.orm.model.association.many;

import com.thoughtworks.orm.Model;

public class Room extends Model {
    private int size;

    public Room() {
    }

    public Room(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;

        Room room = (Room) o;

        if (size != room.size) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return size;
    }

    @Override
    public String toString() {
        return "Room{" +
                "size=" + size +
                '}';
    }
}

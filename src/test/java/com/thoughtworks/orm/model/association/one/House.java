package com.thoughtworks.orm.model.association.one;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;
import com.thoughtworks.orm.model.association.many.Room;

public class House extends Model {
    private int size;
    @HasOne(foreignKey = "house_id", klass = Room.class)
    private Room room;

    public House() {
    }

    public House(int size, Room room) {
        this.size = size;
        this.room = room;
    }

    public House(int size) {
        this.size = size;
        room = new Room(1);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof House)) return false;

        House house = (House) o;

        if (size != house.size) return false;
        if (!room.equals(house.room)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + room.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "House{" +
                "size=" + size +
                ", room=" + room +
                '}';
    }
}

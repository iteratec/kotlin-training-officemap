package de.iteratec.iteraOfficeMap.workplace;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Workplace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(nullable = false)
    private String mapId;
    private int x;
    private int y;
    @Column
    private String equipment;

    public Workplace(String name) {
        super();
        this.setName(name);
    }

    public Workplace(String name, int x, int y, String mapId, String equipment) {
        super();
        this.name = name;
        this.x = x;
        this.y = y;
        this.mapId = mapId;
        this.equipment = equipment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public Workplace() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workplace workplace = (Workplace) o;
        return x == workplace.x &&
                y == workplace.y &&
                Objects.equals(name, workplace.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, x, y);
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}

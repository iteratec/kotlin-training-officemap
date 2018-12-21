package de.iteratec.iteraOfficeMap;

public class WorkplaceDTO {

    public WorkplaceDTO(Workplace workplace) {
        this.id = workplace.getId();
        this.name = workplace.getName();
        this.mapId = workplace.getMapId();
        this.x = workplace.getX();
        this.y = workplace.getY();
    }

    Long id;
    String name;
    String mapId;
    int x;
    int y;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
}

package de.iteratec.iteraOfficeMap;

public class AddWorkplaceDTO {

    public AddWorkplaceDTO() {
    }


    public AddWorkplaceDTO(Workplace workplace) {
        this.name = workplace.getName();
        this.mapId = workplace.getMapId();
        this.x = workplace.getX();
        this.y = workplace.getY();
    }


    int x;
    int y;
    String name;
    String mapId;


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

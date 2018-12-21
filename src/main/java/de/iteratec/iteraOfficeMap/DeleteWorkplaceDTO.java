package de.iteratec.iteraOfficeMap;

public class DeleteWorkplaceDTO {

    public DeleteWorkplaceDTO() {
    }

    public DeleteWorkplaceDTO(Long id) {
        this.id = id;
    }


    Long id;

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }
}

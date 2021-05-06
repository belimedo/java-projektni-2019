package model.personnel;

public class Pilot extends Person {

    private String licenceID;

    public Pilot(String firstName,String lastName,String licenceID) {
        super(firstName,lastName);
        this.licenceID=licenceID;
    }

    public String getLicenceID() {
        return licenceID;
    }

    public void setLicenceID(String licenceID) {
        this.licenceID=licenceID;
    }
}

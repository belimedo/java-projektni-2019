package model.personnel;

public class Passenger extends Person {

    private String passportNumber;

    public Passenger(String firstName, String lastName, String passportNumber) {
        super(firstName,lastName);
        this.passportNumber=passportNumber;
    }

    public String getPassportNumber(){
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber=passportNumber;
    }

}

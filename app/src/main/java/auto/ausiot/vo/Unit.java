package auto.ausiot.vo;


public class Unit {
    public Unit(String id, String userID) {
        this.id = id;
        this.userID = userID;
    }

    private String id;
    private String userID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

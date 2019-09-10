package auto.ausiot.vo;


public class Unit {
    public Unit(String id, String userID) {
        this.id = id;
        this.userID = userID;
    }

    private String id;
    private String userID;
    private String mqqttUserID;
    private String mqqttPassword;
    private String mqqttUrl;

    public String getMqqttUserID() {
        return mqqttUserID;
    }

    public void setMqqttUserID(String mqqttUserID) {
        this.mqqttUserID = mqqttUserID;
    }

    public String getMqqttPassword() {
        return mqqttPassword;
    }

    public void setMqqttPassword(String mqqttPassword) {
        this.mqqttPassword = mqqttPassword;
    }

    public String getMqqttUrl() {
        return mqqttUrl;
    }

    public void setMqqttUrl(String mqqttUrl) {
        this.mqqttUrl = mqqttUrl;
    }

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

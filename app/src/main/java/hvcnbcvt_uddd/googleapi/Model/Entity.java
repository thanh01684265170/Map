package hvcnbcvt_uddd.googleapi.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;

public class Entity {

    @SerializedName("id")
    private String id;

    @SerializedName("from")
    private User userRequest;

    @SerializedName("status")
    private int status;

    @SerializedName("users")
    private List<User> usersHelp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUserRequest() {
        return userRequest;
    }

    public void setUserRequest(User userRequest) {
        this.userRequest = userRequest;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<User> getUsersHelp() {
        return usersHelp;
    }

    public void setUsersHelp(List<User> usersHelp) {
        this.usersHelp = usersHelp;
    }
}

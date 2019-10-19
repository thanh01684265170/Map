
package hvcnbcvt_uddd.googleapi.Model.datasosresponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.Location;
import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;

public class Entity {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}

package hvcnbcvt_uddd.googleapi.Model.datauserlistresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;

public class DataUserSos {
    @SerializedName("status")
    private Long mStatus;
    @SerializedName("users")
    private List<User> mUsers;

    public Long getStatus() {
        return mStatus;
    }

    public void setStatus(Long status) {
        mStatus = status;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        mUsers = users;
    }
}

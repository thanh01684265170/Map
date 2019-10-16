
package hvcnbcvt_uddd.googleapi.Model.dataloginresponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("user")
    private List<User> mUser;

    public List<User> getUser() {
        return mUser;
    }

    public void setUser(List<User> user) {
        mUser = user;
    }

}

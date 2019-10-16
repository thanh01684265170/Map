
package hvcnbcvt_uddd.googleapi.Model.dataloginresponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("user")
    private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

}

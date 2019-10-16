
package hvcnbcvt_uddd.googleapi.Model.dataloginresponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("code")
    private Long mCode;
    @SerializedName("data")
    private UserData mUserData;
    @SerializedName("errors")
    private List<Object> mErrors;
    @SerializedName("message")
    private String mMessage;

    public Long getCode() {
        return mCode;
    }

    public void setCode(Long code) {
        mCode = code;
    }

    public UserData getData() {
        return mUserData;
    }

    public void setData(UserData userData) {
        mUserData = userData;
    }

    public List<Object> getErrors() {
        return mErrors;
    }

    public void setErrors(List<Object> errors) {
        mErrors = errors;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

}

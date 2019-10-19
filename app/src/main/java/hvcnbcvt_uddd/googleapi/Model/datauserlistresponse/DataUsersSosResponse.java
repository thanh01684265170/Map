package hvcnbcvt_uddd.googleapi.Model.datauserlistresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataUsersSosResponse {
    @SerializedName("code")
    private Long mCode;
    @SerializedName("data")
    private DataUserSos mData;
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

    public DataUserSos getData() {
        return mData;
    }

    public void setData(DataUserSos data) {
        mData = data;
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

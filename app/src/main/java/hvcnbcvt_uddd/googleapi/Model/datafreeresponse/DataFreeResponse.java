package hvcnbcvt_uddd.googleapi.Model.datafreeresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataFreeResponse {
    @SerializedName("code")
    private Integer code;
    @SerializedName("data")
    private DataFree data;
    @SerializedName("message")
    private String message;
    @SerializedName("errors")
    private List<Object> errors = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataFree getData() {
        return data;
    }

    public void setData(DataFree data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }
}

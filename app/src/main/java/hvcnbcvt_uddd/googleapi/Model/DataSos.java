package hvcnbcvt_uddd.googleapi.Model;

import com.google.gson.annotations.SerializedName;

public class DataSos {

    @SerializedName("code")
    private Long code;
    @SerializedName("data")
    private Data data;
    @SerializedName("errors")
    private Object errors;
    @SerializedName("message")
    private String message;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

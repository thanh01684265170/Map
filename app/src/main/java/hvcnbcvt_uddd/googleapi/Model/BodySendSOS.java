package hvcnbcvt_uddd.googleapi.Model;

public class BodySendSOS {
    Double latitude;
    Double longitude;
    String content;

    public BodySendSOS(Double latitude, Double longitude, String content) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.content = content;
    }
}
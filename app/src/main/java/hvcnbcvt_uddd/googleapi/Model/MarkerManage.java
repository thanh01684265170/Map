package hvcnbcvt_uddd.googleapi.Model;

public class MarkerManage {
    private double latitude;
    private double longitute;
    private String title;
    private String snippet;
    private int file;

    public MarkerManage() {
    }

    public MarkerManage(double latitude, double longitute, String title, String snippet, int file) {
        this.latitude = latitude;
        this.longitute = longitute;
        this.title = title;
        this.snippet = snippet;
        this.file = file;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }
}

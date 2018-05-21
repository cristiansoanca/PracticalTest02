package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02.model;

public class UrlInformation {

    private String url;

    public UrlInformation(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }
}

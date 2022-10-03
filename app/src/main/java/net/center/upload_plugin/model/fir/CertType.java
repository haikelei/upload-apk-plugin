package net.center.upload_plugin.model.fir;

public class CertType {
    private String key;

    private String token;

    private String upload_url;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setUpload_url(String upload_url) {
        this.upload_url = upload_url;
    }

    public String getUpload_url() {
        return this.upload_url;
    }
}
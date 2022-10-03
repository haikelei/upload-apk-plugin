package net.center.upload_plugin.model.fir;


public class FIRAuthResponse {
    private String id;

    private String type;

//    private String short;

    private Cert cert;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setCert(Cert cert) {
        this.cert = cert;
    }

    public Cert getCert() {
        return this.cert;
    }
}


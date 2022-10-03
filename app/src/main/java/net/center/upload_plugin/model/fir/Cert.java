package net.center.upload_plugin.model.fir;

public class Cert {
    private CertType icon;

    private CertType binary;

    public void setIcon(CertType icon) {
        this.icon = icon;
    }

    public CertType getIcon() {
        return this.icon;
    }

    public void setBinary(CertType binary) {
        this.binary = binary;
    }

    public CertType getBinary() {
        return this.binary;
    }
}
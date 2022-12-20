package net.center.upload_plugin.model.fir;

import com.google.gson.annotations.SerializedName;

import com.sun.jndi.toolkit.url.Uri;
import net.center.upload_plugin.interfaces.SendMsgInterface;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FIRAppListInfo implements SendMsgInterface {

    public String id;
    public String user_id;
    public String type;
    public String name;
    @SerializedName("short")
    public String shortX;
    public String bundle_id;
    public String download_domain;
    public Integer genre_id;
    public Boolean is_opened;
    public String web_template;
    public Boolean has_combo;
    public Boolean download_domain_https_ready;
    public Integer created_at;
    public String icon_url;
    public MasterReleaseBean master_release;

    @Override
    public String getBuildName() {
        return name;
    }

    @Override
    public String getBuildCreated() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(created_at*1000L);
        return simpleDateFormat.format(date);
    }

    @Override
    public String getBuildQRCodeURL() {
        return "";
    }

    @Override
    public String getBuildVersion() {
        return master_release.version;
    }

    @Override
    public String getBuildShortcutUrl() {
        String scheme = "http://";
        String path = download_domain + "/" + shortX;
        return scheme + path;
    }

    @Override
    public String getBuildKey() {
        return "";
    }


    public static class MasterReleaseBean {
        public String version;
        public String build;
        public String release_type;
        public String distribution_name;
        public Object supported_platform;
        public Integer created_at;
    }
}

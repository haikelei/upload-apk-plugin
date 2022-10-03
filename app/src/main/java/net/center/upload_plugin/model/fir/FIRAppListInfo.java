package net.center.upload_plugin.model.fir;

import com.google.gson.annotations.SerializedName;

import net.center.upload_plugin.interfaces.SendMsgInterface;

public class FIRAppListInfo implements SendMsgInterface {

    public String id;
    public String user_id;
    public String type;
    public String name;
    @SerializedName("short")
    public String shortX;
    public String bundle_id;
    public Integer genre_id;
    public Boolean is_opened;
    public String web_template;
    public Boolean has_combo;
    public Integer created_at;
    public String icon_url;
    public MasterReleaseBean master_release;

    @Override
    public String getBuildName() {
        return name;
    }

    @Override
    public String getBuildCreated() {
        return created_at + "";
    }

    @Override
    public String getBuildQRCodeURL() {
        return shortX;
    }

    @Override
    public String getBuildVersion() {
        return master_release.build;
    }

    @Override
    public String getBuildShortcutUrl() {
        return icon_url;
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

package net.center.upload_plugin.interfaces;

import com.android.tools.r8.graph.S;

public interface SendMsgInterface {

    String getBuildName();

    String getBuildCreated();

    String getBuildQRCodeURL();

    String getBuildVersion();

    String getBuildShortcutUrl();
    String getBuildKey();
}

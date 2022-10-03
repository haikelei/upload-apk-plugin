package net.center.upload_plugin.model.fir;

import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * <p>
 * fir.im上传参数设置
 */
public class UploadFirParams {

    //长度为 32, 用户在 fir 的 api_token
    public String apiToken;


    public UploadFirParams() {

    }

    public UploadFirParams(String apiToken) {
        this.apiToken = apiToken;
    }

    public static UploadFirParams getConfig(Project project) {
        UploadFirParams extension = project.getExtensions().findByType(UploadFirParams.class);
        if (extension == null) {
            extension = new UploadFirParams();
        }
        return extension;
    }

}

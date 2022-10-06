package net.center.upload_plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;

import net.center.upload_plugin.model.fir.UploadFirParams;
import net.center.upload_plugin.model.UploadPgyParams;
import net.center.upload_plugin.params.GitLogParams;
import net.center.upload_plugin.params.SendDingParams;
import net.center.upload_plugin.params.SendFeishuParams;
import net.center.upload_plugin.params.SendWeixinGroupParams;

import org.apache.http.util.TextUtils;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * 2021/9/3.
 */
public class UploadApkPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        UploadPgyParams uploadPGYParams = project.getExtensions().create(PluginConstants.UPLOAD_PGY_PARAMS_NAME, UploadPgyParams.class);
        UploadFirParams uploadFirParams = project.getExtensions().create(PluginConstants.UPLOAD_FIR_PARAMS_NAME, UploadFirParams.class);

        createParams(project);
        project.afterEvaluate(project1 -> {
            AppExtension appExtension = ((AppExtension) project1.getExtensions().findByName(PluginConstants.ANDROID_EXTENSION_NAME));
            if (appExtension == null) {
                return;
            }
            DomainObjectSet<ApplicationVariant> appVariants = appExtension.getApplicationVariants();
            for (ApplicationVariant applicationVariant : appVariants) {
                if (applicationVariant.getBuildType() != null) {
                    if (!TextUtils.isEmpty(uploadPGYParams.apiKey)) {
                        dependsOnTask(applicationVariant, uploadPGYParams, project1);
                    } else if (!TextUtils.isEmpty(uploadFirParams.apiToken)) {
                        dependsOnTask(applicationVariant, uploadFirParams, project1);
                    }

                }
            }
        });
    }

    private void createParams(Project project){
        project.getExtensions().create(PluginConstants.GIT_LOG_PARAMS_NAME, GitLogParams.class);
        project.getExtensions().create(PluginConstants.DING_PARAMS_NAME, SendDingParams.class);
        project.getExtensions().create(PluginConstants.FEISHU_PARAMS_NAME, SendFeishuParams.class);
        project.getExtensions().create(PluginConstants.WEIXIN_GROUP_PARAMS_NAME, SendWeixinGroupParams.class);
    }


    private void dependsOnTask(ApplicationVariant applicationVariant, UploadPgyParams uploadParams, Project project1) {
        String variantName =
                applicationVariant.getName().substring(0, 1).toUpperCase() + applicationVariant.getName().substring(1);
        if (PluginUtils.isEmpty(variantName)) {
            variantName = PluginUtils.isEmpty(uploadParams.buildTypeName) ? "Release" : uploadParams.buildTypeName;
        }
        //创建我们，上传到蒲公英的task任务
        UploadPGYTask uploadPGYTask = project1.getTasks()
                .create(PluginConstants.TASK_EXTENSION_NAME + variantName, UploadPGYTask.class);
        uploadPGYTask.init(applicationVariant, project1);

        //依赖关系 。上传依赖打包，打包依赖clean。
        applicationVariant.getAssembleProvider().get().dependsOn(project1.getTasks().findByName("clean"));
        uploadPGYTask.dependsOn(applicationVariant.getAssembleProvider().get());
    }


    private void dependsOnTask(ApplicationVariant applicationVariant, UploadFirParams uploadFirParams, Project project1) {
        String variantName =
                applicationVariant.getName().substring(0, 1).toUpperCase() + applicationVariant.getName().substring(1);
        //创建我们，上传到蒲公英的task任务
        UploadFIRTask uploadFIRTask = project1.getTasks()
                .create(PluginConstants.TASK_EXTENSION_NAME + variantName, UploadFIRTask.class);
        uploadFIRTask.init(applicationVariant, project1);

        //依赖关系 。上传依赖打包，打包依赖clean。
        applicationVariant.getAssembleProvider().get().dependsOn(project1.getTasks().findByName("clean"));
        uploadFIRTask.dependsOn(applicationVariant.getAssembleProvider().get());
    }
}

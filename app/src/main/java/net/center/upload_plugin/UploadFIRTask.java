package net.center.upload_plugin;


import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.center.upload_plugin.helper.CmdHelper;
import net.center.upload_plugin.helper.HttpHelper;
import net.center.upload_plugin.helper.SendMsgHelper;
import net.center.upload_plugin.model.fir.*;
import net.center.upload_plugin.net.OkHttpUtils;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import org.apache.http.util.TextUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Android-ZX
 * 2021/9/3.
 */
public class UploadFIRTask extends DefaultTask {

    private BaseVariant mVariant;
    private Project mTargetProject;

    public void init(BaseVariant variant, Project project) {
        this.mVariant = variant;
        this.mTargetProject = project;
        setDescription(PluginConstants.TASK_DES);
        setGroup(PluginConstants.TASK_GROUP_NAME);
    }

    @TaskAction
    public void uploadToFIR() {
        for (BaseVariantOutput output : mVariant.getOutputs()) {
            File apkDir = output.getOutputFile();
            if (apkDir == null || !apkDir.exists()) {
                throw new GradleException("apkDir OutputFile is not exist!");
            }
            System.out.println("apkDir path: " + apkDir.getAbsolutePath());
            File apk = null;
            if (apkDir.getName().endsWith(".apk")) {
                apk = apkDir;
            } else {
                if (apkDir.listFiles() != null) {
                    for (int i = Objects.requireNonNull(apkDir.listFiles()).length - 1; i >= 0; i--) {
                        File apkFile = Objects.requireNonNull(apkDir.listFiles())[i];
                        if (apkFile != null && apkFile.exists() && apkFile.getName().endsWith(".apk")) {
                            apk = apkFile;
                            break;
                        }
                    }
                }
            }
            if (apk == null || !apk.exists()) {
                throw new GradleException("apk file is not exist!");
            }
            System.out.println("final upload apk path: " + apk.getAbsolutePath());
            UploadFirParams params = UploadFirParams.getConfig(mTargetProject);

            try {
                ApkFile apkFile = new ApkFile(apk);
                ApkMeta apkMeta = apkFile.getApkMeta();
                uploadFIR(params,apkMeta,apk);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadFIR(UploadFirParams params, ApkMeta apkMeta, File apk) {
        String result = OkHttpUtils.builder().url("http://api.bq04.com/apps")
                .addParam("type", "android")
                .addParam("bundle_id", apkMeta.getPackageName())
                .addParam("api_token", params.apiToken)
                .post(true)
                .async();
        System.out.println("upload fir --- 发布应用获取上传凭证: " + result);
        FIRAuthResponse firAuthResponse = new Gson().fromJson(result, FIRAuthResponse.class);
        if (TextUtils.isEmpty(firAuthResponse.getId())) {
            System.out.println("upload fir --- 发布应用获取上传凭证失败: ");
            return;
        }
        uploadFileToFIR(params,apkMeta,firAuthResponse,apk);
    }

    private void uploadFileToFIR(UploadFirParams params, ApkMeta apkMeta, FIRAuthResponse firAuthResponse, File apk) {
        CertType certType = firAuthResponse.getCert().getBinary();
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("key", certType.getKey());
        bodyBuilder.addFormDataPart("token", certType.getToken());
        bodyBuilder.addFormDataPart("x:name", apkMeta.getName());
        bodyBuilder.addFormDataPart("x:version", apkMeta.getVersionName());
        bodyBuilder.addFormDataPart("x:build", apkMeta.getVersionCode()+"");
        bodyBuilder.addFormDataPart("file", apk.getName(), RequestBody
                .create(MediaType.parse("*/*"), apk));

        Request request = getRequestBuilder()
                .url(certType.getUpload_url())
                .post(bodyBuilder.build())
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                response.body().close();
                System.out.println("upload fir --- 上传应用结果: " + result);
                if (!PluginUtils.isEmpty(result)) {
                    FIRUploadResponse firUploadResponse = new Gson().fromJson(result, FIRUploadResponse.class);
                    if (!firUploadResponse.is_completed) {
                        System.out.println("upload fir --- 上传应用失败: ");
                        return;
                    }
                    checkFIRUploadBuildInfo(params,apkMeta);
                    System.out.println("upload fir --- 上传应用成功: ");
                }
            } else {
                System.out.println("upload fir ---- 失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void checkFIRUploadBuildInfo(UploadFirParams uploadFirParams, ApkMeta apkMeta) {
        //1.获取全部应用列表
        String res = OkHttpUtils.builder().url("http://api.bq04.com/apps?api_token=" + uploadFirParams.apiToken)
                .get()
                .sync();
        System.out.println("upload fir --- 获取全部应用列表:"+ res);
        FirAppListResponse firAppListResponse = new Gson().fromJson(res, FirAppListResponse.class);
        List<FIRAppListInfo> appListInfoList = firAppListResponse.items;
        //2.根据上传应用包名查找
        FIRAppListInfo firAppListInfo = null;
        for (int i = 0; i < appListInfoList.size(); i++) {
            if (apkMeta.getPackageName().equals(appListInfoList.get(i).bundle_id)) {
                firAppListInfo = appListInfoList.get(i);
            }
        }
        if (firAppListInfo == null) {
            System.out.println("upload fir --- buildInfo: FIR应用列表中没有找到"+apkMeta.getPackageName());
            return;
        }
        //3.
        String gitLog = CmdHelper.checkGetGitParamsWithLog(mTargetProject);
        SendMsgHelper.sendMsgToDingDing(mTargetProject, firAppListInfo, gitLog);
        SendMsgHelper.sendMsgToFeishu(mTargetProject, firAppListInfo, gitLog);
        SendMsgHelper.sendMsgToWeiXinGroup(mTargetProject, firAppListInfo, gitLog);
        System.out.println("上传成功，应用链接: ");

    }


    private Request.Builder getRequestBuilder() {
        return new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8");
    }


}
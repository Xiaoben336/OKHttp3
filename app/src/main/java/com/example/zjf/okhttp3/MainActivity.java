package com.example.zjf.okhttp3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private OkHttpClient mOkHttpClient;
    private Button bt_send;
    private Button bt_postsend;
    private Button bt_sendfile;
    private Button bt_downfile;
    //上传文件本身也是一个POST请求,首先定义上传文件类型：
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initOkHttpClient();
        bt_send = (Button)findViewById(R.id.bt_send);
        bt_postsend = (Button)findViewById(R.id.bt_postsend);
        bt_sendfile = (Button)findViewById(R.id.bt_sendfile);
        bt_downfile = (Button)findViewById(R.id.bt_downfile);

        bt_send.setOnClickListener(this);
        bt_postsend.setOnClickListener(this);
        bt_sendfile.setOnClickListener(this);
        bt_downfile.setOnClickListener(this);
    }

    /**
     * 设置超时时间和缓存
     */
    private void initOkHttpClient() {
        File sdCache = getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .cache(new Cache(sdCache.getAbsoluteFile(),cacheSize));
        mOkHttpClient = builder.build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_send:
                getAsynHttp();
                break;
            case R.id.bt_postsend:
                postAsynHttp();
                break;
            case R.id.bt_sendfile:
                postAsynFile();
                break;
            case R.id.bt_downfile:
                downAsynFile();
                break;
                default:
                    break;
        }
    }

    private void downAsynFile() {
        mOkHttpClient = new OkHttpClient();

        String url = "http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg";
        Request request = new Request.Builder()
                .url(url)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File("/sdcard/wangshu.jpg"));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    Log.i("wangshu", "IOException");
                    e.printStackTrace();
                }
                Log.d("wangshu", "文件下载成功");
            }
        });
    }

    private void postAsynFile() {

    }

    /**
     * 异步POST请求
     */
    private void postAsynHttp() {
        //创建OkHttpClient
        mOkHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("size","10")
                .build();

        Request request = new Request.Builder()
                .url("http:www.baidu.com")
                .post(formBody)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "post异步请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.i("zjf", str);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "post异步请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 异步get请求
     */
    private void getAsynHttp() {
        //创建OkHttpClient
        mOkHttpClient = new OkHttpClient();
        //创建Request
        Request.Builder requestBuilder = new Request.Builder()
                .url("http://www.baidu.com");
        final Request request = requestBuilder.build();

        //创建Call
        Call mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.cacheResponse() != null){
                    String str = response.cacheResponse().toString();
                    Log.i("zjf", "cache --- " + str);
                }else {
                    response.body().string();
                    String str = response.networkResponse().toString();
                    Log.i("zjf","network --- " + str);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"get异步请求成功" ,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

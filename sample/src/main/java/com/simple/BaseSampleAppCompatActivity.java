package com.simple;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.woodys.http.core.exception.HttpError;
import com.okay.sampletamplate.SampleAppCompatActivity;
import com.simple.json.GsonUtils;
import com.simple.net.callback.ILoadingView;
import com.simple.ui.progress.ProgressDialogFragment;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class BaseSampleAppCompatActivity extends SampleAppCompatActivity implements ILoadingView {

    protected ProgressDialogFragment progressDialog = ProgressDialogFragment.Companion.newInstance("加载中...");

    protected TextView requestState;
    protected TextView requestHeaders;
    protected TextView responseData;
    protected TextView responseHeader;
    protected FrameLayout rootContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_base);
        Window window = getWindow();
        requestState = (TextView) window.findViewById(R.id.requestState);
        requestHeaders = (TextView) window.findViewById(R.id.requestHeaders);
        responseData = (TextView) window.findViewById(R.id.responseData);
        responseHeader = (TextView) window.findViewById(R.id.responseHeader);
        rootContent = (FrameLayout) window.findViewById(R.id.content);
        onActivityCreate(savedInstanceState);
    }

    protected abstract void onActivityCreate(Bundle savedInstanceState);


    @Override
    public View findViewById(int id) {
        return rootContent.findViewById(id);
    }

    private void clearContentView() {
        rootContent.removeAllViews();
    }

    @Override
    public void setContentView(int layoutResID) {
        clearContentView();
        getLayoutInflater().inflate(layoutResID, rootContent, true);
    }

    @Override
    public void setContentView(View view) {
        clearContentView();
        rootContent.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        clearContentView();
        rootContent.addView(view, params);
    }

    /**
     * 显示加载
     */
    @Override
    public void showLoading(){
        progressDialog.show(getSupportFragmentManager());
    };

    /**
     * 隐藏加载
     */
    @Override
    public void hideLoading(){
        progressDialog.dismiss();
    };


    protected <T> void handleResponse(Response response,T body) {
        StringBuilder sb;
        if (body == null) {
            responseData.setText("--");
        } else {
            if (body instanceof String) {
                responseData.setText((String) body);
            } else if (body instanceof List) {
                sb = new StringBuilder();
                List list = (List) body;
                for (Object obj : list) {
                    sb.append(obj.toString()).append("\n");
                }
                responseData.setText(sb.toString());
            } else if (body instanceof Set) {
                sb = new StringBuilder();
                Set set = (Set) body;
                for (Object obj : set) {
                    sb.append(obj.toString()).append("\n");
                }
                responseData.setText(sb.toString());
            } else if (body instanceof Map) {
                sb = new StringBuilder();
                Map map = (Map) body;
                Set keySet = map.keySet();
                for (Object key : keySet) {
                    sb.append(key.toString()).append(" ： ").append(map.get(key)).append("\n");
                }
                responseData.setText(sb.toString());
            } else if (body instanceof File) {
                File file = (File) body;
                responseData.setText("数据内容即为文件内容\n下载文件路径：" + file.getAbsolutePath());
            } else if (body instanceof Bitmap) {
                responseData.setText("图片的内容即为数据");
            } else if(body instanceof ResponseBody){
                Charset UTF8 = Charset.forName("UTF-8");
                BufferedSource source = ((ResponseBody)body).source();
                try {
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = ((ResponseBody)body).contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                if (isPlaintext(buffer)) {
                    responseData.setText(buffer.clone().readString(charset));
                }else {
                    responseData.setText("非字符串数据，暂时无法显示！");
                }

            } else {
                responseData.setText(GsonUtils.toJson(body));
            }
        }
        Request request = null;
        okhttp3.Response rawResponse = response.raw();
        if (rawResponse != null) {
            request = rawResponse.request();

            Headers responseHeadersString = rawResponse.headers();
            Set<String> names = responseHeadersString.names();
            sb = new StringBuilder();
            sb.append("url ： ").append(rawResponse.request().url()).append("\n\n");
            sb.append("stateCode ： ").append(rawResponse.code()).append("\n");
            for (String name : names) {
                sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
            }
            responseHeader.setText(sb.toString());
        } else {
            responseHeader.setText("--");
        }

        if (request != null) {
            requestState.setText("请求成功  请求方式：" + request.method() + "\n" + "url：" + request.url());

            Headers requestHeadersString = request.headers();
            Set<String> requestNames = requestHeadersString.names();
            sb = new StringBuilder();
            for (String name : requestNames) {
                sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
            }
            requestHeaders.setText(sb.toString());
        } else {
            requestState.setText("--");
            requestHeaders.setText("--");
        }
    }

    protected void handleError(Request request, Throwable error) {
        if (error != null) {
            error.printStackTrace();
        }
        StringBuilder sb;
        responseData.setText("--");
        okhttp3.Response rawResponse = null;
        if(null!=error && error instanceof HttpError){
            if(((HttpError)error).body instanceof HttpException){
                rawResponse = ((HttpException)((HttpError)error).body).response().raw();
            }
        }

        if (rawResponse != null) {
            request = rawResponse.request();
            Headers responseHeadersString = rawResponse.headers();
            Set<String> names = responseHeadersString.names();
            sb = new StringBuilder();
            sb.append("stateCode ： ").append(rawResponse.code()).append("\n");
            for (String name : names) {
                sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
            }
            responseHeader.setText(sb.toString());
        } else {
            responseHeader.setText("--");
        }

        if (request != null) {
            requestState.setText("请求失败  请求方式：" + request.method() + "\n" + "url：" + request.url());

            Headers requestHeadersString = request.headers();
            Set<String> requestNames = requestHeadersString.names();
            sb = new StringBuilder();
            for (String name : requestNames) {
                sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
            }
            requestHeaders.setText(sb.toString());
        } else {
            requestState.setText("--");
            requestHeaders.setText("--");
        }

    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}

package com.simple.net.protocol;

import com.woodys.http.core.Call;
import com.simple.net.UrlConstant;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface UploadApiService {

    @Multipart
    @POST(UrlConstant.URI_USER_UPLOAD_AVATAR)
    Call<ResponseBody> uploadAvatar(@HeaderMap Map<String, String> headers, @Part List<MultipartBody.Part> params);

    @Multipart
    @POST("http://1.w2wz.com/upload.php")
    Call<ResponseBody> uploadFile(@Part("MAX_FILE_SIZE") Long max_file_size, @Part List<MultipartBody.Part> params);

}

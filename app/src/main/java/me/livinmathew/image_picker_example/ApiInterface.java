package me.livinmathew.image_picker_example;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
/*
    @Multipart
    @POST("uploadimages.php")
    Call<ResponsePOJO> uploadImage(@Part MultipartBody.Part image);
 */

    @FormUrlEncoded
    @POST("uploadimages.php")
    Call<ResponsePOJO> uploadIm(@Field("name") String name, @Field("image") String image);
   //
}
package me.livinmathew.image_picker_example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponsePOJO {

        @SerializedName("name")
        String Name;
        @SerializedName("image")
        String Image;
        @SerializedName("success")
        @Expose
        private Integer success;
        @SerializedName("message")
        @Expose
        private String message;


        public String getImage() {
                return Image;
        }

        public void setImage(String image) {
                Image = image;
        }

        public Integer getSuccess() {
                return success;
        }

        public void setSuccess(Integer success) {
                this.success = success;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }
 /*
    private boolean status;
    private String remarks;

    public boolean isStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

  */
}

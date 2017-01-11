package com.example.android.androidbooks;

/**
 * Created by saurabh on 1/11/2017.
 */

public class CustomObject {
    private String mtitle;
    private String mAuthor;
    private String mDescrip;
    private String mPreviewUrl;
    private String mImageUrl;

    public CustomObject(String title,String Author,String Descrip, String PreviewUrl,String ImageUrl){
        mtitle = title;
        mAuthor = Author;
        mDescrip = Descrip;
        mPreviewUrl = PreviewUrl;
        mImageUrl = ImageUrl;
    }

    public String getMtitle(){
        return mtitle;
    }
    public String getmAuthor(){
        return mAuthor;
    }
    public String getmDescrip(){
        return mDescrip;
    }
    public String getmPreviewUrl(){
        return mPreviewUrl;
    }
    public String getmImageUrl(){
        return mImageUrl;
    }

}

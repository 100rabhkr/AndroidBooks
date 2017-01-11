package com.example.android.androidbooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by saurabh on 1/11/2017.
 */

public class CustomAdapter extends ArrayAdapter<CustomObject> {

    public CustomAdapter(Context context, ArrayList<CustomObject> custom){

        super(context,0,custom);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View Customview = convertView;
        if (Customview==null){
            Customview = LayoutInflater.from(getContext()).inflate(R.layout.list,parent,false);

        }

        CustomObject currentItem = getItem(position);
        ImageView logo = (ImageView) Customview.findViewById(R.id.imageicon);
        TextView NameView = (TextView) Customview.findViewById(R.id.titleTextView);
        String Title = currentItem.getMtitle();
        NameView.setText(Title);

        TextView AuthorView = (TextView) Customview.findViewById(R.id.authoutTextView);
        String Author = currentItem.getmAuthor();
        AuthorView.setText(Author);

        TextView DescView = (TextView) Customview.findViewById(R.id.despTextView);
        String Desc = currentItem.getmDescrip();
        DescView.setText(Desc);

        String wrongUrl=currentItem.getmImageUrl();
        if(wrongUrl!=null){
        URL url = null;
        try {
            url = new URL(wrongUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URI uri = null;
        try {
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            url = uri.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            logo.setImageBitmap(myBitmap);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        else {
            logo.setImageResource(R.mipmap.ic_launcher);
        }

        return Customview;



    }
}

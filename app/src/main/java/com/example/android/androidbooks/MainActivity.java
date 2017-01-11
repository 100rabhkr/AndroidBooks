package com.example.android.androidbooks;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the USGS dataset for earthquake information */
    private static final String USGS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10&prettyPrint=false";

    ArrayList<CustomObject> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BookAsyncTask task = new BookAsyncTask();
        task.execute();
    }

    private void makelist(final ArrayList<CustomObject> book){
        books = book;
        CustomAdapter adapter = new CustomAdapter(this,book);
        ListView bookListView = (ListView) findViewById(R.id.list);
        bookListView.setAdapter(adapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomObject customObject = book.get(i);
                final String url = customObject.getmPreviewUrl();
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(customObject.getMtitle());
                alertDialog.setMessage("Preview the book in your Browser");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Proceed",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse(url));

                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }


                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

    }



    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<CustomObject>> {

        @Override
        protected ArrayList<CustomObject> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(USGS_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            ArrayList<CustomObject> bookal ;
            bookal = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return bookal;
        }

        @Override
        protected void onPostExecute(ArrayList<CustomObject> customObjects) {
            if (customObjects == null) {
                return;
            }
            makelist(customObjects);

        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if(url == null){
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<CustomObject> extractFeatureFromJson(String bookJSON) {

            ArrayList<CustomObject> bookCustomObject = new ArrayList<>();
            String titlename;
            String Authorname;
            String Descriptionlong;
            String previewUrl;
            String imageUrl;

            if(TextUtils.isEmpty(bookJSON)){
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray itemArray = baseJsonResponse.getJSONArray("items");
                for (int i =0; i<itemArray.length();i++) {

                    JSONObject currentbook = itemArray.getJSONObject(i);
                    JSONObject bookinfo = currentbook.getJSONObject("volumeInfo");
                    if(bookinfo.has("title")){
                    titlename = bookinfo.getString("title");}
                    else {
                        titlename = "Not Found";
                    }
                    if(bookinfo.has("authors")){

                    JSONArray authorarray = bookinfo.getJSONArray("authors");
                    String[] Sauthor = new String[authorarray.length()];

                    for(int j=0;j<authorarray.length();j++){
                        Sauthor[j]= authorarray.getString(j);
                    }

                    Authorname = arrtostrng(Sauthor);}
                    else if (bookinfo.has("publisher")){
                            Authorname = bookinfo.getString("publisher");
                    }
                    else {
                        Authorname = "Not Available";
                    }

                    if(currentbook.has("searchInfo")){
                        JSONObject information = currentbook.getJSONObject("searchInfo");
                        if (information.has("textSnippet")) {
                            Descriptionlong = information.getString("textSnippet");
                        }

                        else {
                            Descriptionlong = "Not Available for this book";
                        }
                    }
                    else {
                        Descriptionlong = "Not Available";
                    }
                    if(currentbook.has("accessInfo")) {
                        JSONObject saleinfo = currentbook.getJSONObject("accessInfo");
                        if(saleinfo.has("webReaderLink")){
                        previewUrl = saleinfo.getString("webReaderLink");}
                        else {
                            previewUrl = "https://www.google.com/null";
                        }
                    }
                    else {
                        previewUrl = "https://www.google.com/null";
                    }

                    if(currentbook.has("imageLinks")){
                        JSONObject imageinfo = currentbook.getJSONObject("imageLinks");
                        if (imageinfo.has("thumbnail")||imageinfo.has("smallThumbnail")){
                            if(imageinfo.has("thumbnail")){
                                imageUrl = imageinfo.getString("thumbnail");
                            }
                            else {
                                imageUrl = imageinfo.getString("smallThumbnail");
                            }
                        }
                        else {
                            imageUrl = null;
                        }
                    }
                    else {
                        imageUrl = null;
                    }
                    CustomObject customObject = new CustomObject(titlename,Authorname,Descriptionlong,previewUrl,imageUrl);
                    bookCustomObject.add(customObject);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }

            return bookCustomObject;

        }

        public String arrtostrng(String[] arr){
            String temp = arr[0] ;
            for(int i =1; i<arr.length; i++){
                if(arr[i]==null){
                    break;
                }
                else {
                    temp = temp + ", " + arr[i];
                }
            }
            temp = temp+".";
            return temp;
        }

       /* public String strtoshort(String longstr){
            String temp;
            if (longstr.contains(".")){
               String temparr[] = longstr.split(".");
                temp = temparr[0].trim();
            }

            else{
                temp = longstr;
            }

            return temp;
        }*/

    }


}

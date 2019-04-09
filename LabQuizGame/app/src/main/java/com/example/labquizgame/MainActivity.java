package com.example.labquizgame;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class MainActivity extends AppCompatActivity {

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;

            // Agregar permiso en AndroidManifest.xml
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Esto es muy estilo C
                // Se lee un caracter a la vez (como cuando se hace gets() en C o C++)
                int data = inputStreamReader.read();
                while (data != -1){
                    char character = (char)data;
                    result += character;
                    data = inputStreamReader.read();
                }

                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return "Error";
            }
        }
    }

    private void getCharacters(String html){
        Log.i("Log", "Parsing document.");

        Document document = Jsoup.parse(html);
        Elements characters = document.select("div .entry-content p span strong");
        //Elements images = document.select("");

        //images.get(0).attr("src");

        String characterLine = "";
        String characterList [];
        for(int i = 0 ; i < characters.size(); ++i){
            characterLine = characters.get(i).text();
            characterList = characterLine.split(" ");
            characterLine = characterList[characterList.length] + " " + characterList[characterList.length-1];
            Log.i("Log", characterLine);
        }


        Log.i("Log", "Document parsed.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Log", "starting");

        DownloadTask downloadTask = new DownloadTask();
        String result = null;
        try {
            result = downloadTask.execute("https://tristanfrench.wordpress.com/2016/04/23/jojos-bizarre-adventure-ranking-each-jojo-from-worst-to-best/").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("Log", result);
        //getCharacters(result);
    }
}

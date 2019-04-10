package com.example.labquizgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> characters = new ArrayList<String>();
    private ArrayList<String> images = new ArrayList<String>();
    private int characterOrder [];
    private int currentCharacter;
    boolean finishedGame;


    public void submitResult(View view){
        Button button = (Button) view;

        if(finishedGame){
            characterOrder = getRandomList(characters.size());
            currentCharacter = 0;
            finishedGame = false;
            button.setText("Submit");
            setRound();
        } else {

            RadioGroup options = findViewById(R.id.responseGroup);
            RadioButton response = (RadioButton) findViewById(options.getCheckedRadioButtonId());
            if (response != null) {
                if (response.getText() == characters.get(characterOrder[currentCharacter])) {
                    Toast.makeText(this, "Respuesta correcta.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Respuesta incorrecta.", Toast.LENGTH_SHORT).show();
                }

                ++currentCharacter;

                if (currentCharacter == characters.size()) {
                    Toast.makeText(this, "Juego terminado", Toast.LENGTH_LONG).show();
                    button.setText("Reiniciar juego.");
                    finishedGame = true;
                } else {
                    setRound();
                }
            }
        }
    }



    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {
                Document document = Jsoup.connect(urls[0]).get();
                result = document.toString();
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return "Error";
            }


        }
    }


    public class ChargeImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;

            try {

                InputStream inputStream = new java.net.URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }
    }

    private void resetGame(){

    }

    private void getCharacters(String html){
        Log.i("Log", "Parsing document.");

        Document document = Jsoup.parse(html);
        Elements characterElements = document.select("div .entry-content p span strong");

        Elements imageElements = document.select("div .entry-content p img");




        String characterLine = "";
        String stringList [] = new String[2];
        for(int i = 0 ; i < characterElements.size()-1; ++i){

            Log.i("Log", characterElements.get(i).text());
            characterLine = characterElements.get(i).text();
            characterLine = characterLine.substring(characterLine.lastIndexOf("-")+1);
            characterLine = characterLine.substring(characterLine.lastIndexOf("–")+1);

            if(i == 5){
                stringList = characterLine.split("and");
                characters.add(stringList[0]); // numero 3
                characters.add(stringList[1]); // numero 2
            } else {
                characters.add(characterLine);


            }

        }

        images.add(imageElements.get(0).attr("src"));
        images.add(imageElements.get(1).attr("src"));
        images.add(imageElements.get(3).attr("src"));
        images.add(imageElements.get(4).attr("src"));
        images.add(imageElements.get(6).attr("src"));
        images.add(imageElements.get(8).attr("src")); // 3
        images.add(imageElements.get(9).attr("src")); // 2
        images.add(imageElements.get(10).attr("src"));




        Log.i("Log", "Document parsed.");
    }



    private int[] getRandomList(int size){
        Random random = new Random();
        int results [] = new int [size];
        for(int i = 0; i < size; ++i){
            results[i] = -1;
        }
        results[0] = random.nextInt(size);
        //Log.i("Log", Integer.toString(0) + ": " + Integer.toString(results[0]));

        for(int i = 1 ; i < size ; ++i){
            //Log.i("Current", Integer.toString(i));
            results[i] = recursiveChecking(results, random.nextInt(size), i);
            //Log.i("Log", Integer.toString(i) + ": " + Integer.toString(results[i]));
        }
        //Log.i("Log", "terminado");
        return results;
    }

    private int recursiveChecking(int results[], int number, int currentElment){
        Random random = new Random();

        int previousElement = 0;
        while(number != results[previousElement] && previousElement < currentElment){
            //Log.i("Cycle", Integer.toString(number));
            //Log.i("Cycle", Integer.toString(results[previousElement]));
            ++previousElement;
        }

        if(number == results[previousElement]){
            //Log.i("Log", results[previousElement]+ ": Tried " + Integer.toString(number) + " but failed" );
            return recursiveChecking(results, random.nextInt(results.length), currentElment);
        } else {
            return number;
        }
    }



    private void setRound(){
        Random random = new Random();

        RadioButton firstOption = findViewById(R.id.firstOption);
        RadioButton secondOption = findViewById(R.id.secondOption);
        RadioButton thirdOption = findViewById(R.id.thirdOption);
        RadioButton fourthOption = findViewById(R.id.fourthOption);


        int randomNumber = 0;
        int correctAnswerIndex = characterOrder[currentCharacter];
        String options [] = new String [4];
        randomNumber = random.nextInt(4);

        //Carga la respuesta correcta
        options[randomNumber] = characters.get(correctAnswerIndex);         //Pone la respuesta correcta


        ChargeImageTask chargeImageTask = new ChargeImageTask();
        Bitmap bitmap = null;
        try {
            bitmap = chargeImageTask.execute(images.get(correctAnswerIndex)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ImageView image = (ImageView) findViewById(R.id.characterImage);
        image.setImageBitmap(bitmap);                                        //Pone la imagen de la respuesta correcta

        Log.i("Log", characters.get(correctAnswerIndex));
        Log.i("Log", images.get(correctAnswerIndex));


        for(int i = 0 ; i < 3 ; ++i){
            randomNumber = random.nextInt(characters.size());

            if(options[i] == null){
                options[i] = characters.get(randomNumber);
            } else {
                options[i+1] = characters.get(randomNumber);
            }
        }

        firstOption.setText(options[0]);
        secondOption.setText(options[1]);
        thirdOption.setText(options[2]);
        fourthOption.setText(options[3]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Log", "starting");

        finishedGame = false;
        DownloadTask downloadTask = new DownloadTask();
        String result = null;
        try {
            result = downloadTask.execute("https://tristanfrench.wordpress.com/2016/04/23/jojos-bizarre-adventure-ranking-each-jojo-from-worst-to-best/").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        getCharacters(result);
        characterOrder = getRandomList(characters.size());
        currentCharacter = 0;
        setRound();
    }
}

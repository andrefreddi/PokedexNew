package com.example.apppokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String feedUrl = "https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json";
    private int feedLimit = 10;
    private String feedType = "topfreeapplications";
    private ListView rssListView;

    @Override  //o onCreate irá fazer o download!
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rssListView = findViewById(R.id.rssListView);

        //asssim que a tela for criada será executado esse trecho de cód.
        downloadUrl(String.format(feedUrl, feedType, feedLimit)); //funciona como sprintf do C
     }
    //metodo chamado quando é hora de "inflar o menu
    //Vincular o arquivo de leiaute do menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.feeds_menu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.menuFree:
                feedType = "topfreeapplications";
                break;
            case R.id.menuPaid:
                feedType = "toppaidapplications";
                break;
            case R.id.menuSongs:
                feedType = "topsongs";
                break;
            case R.id.menu10:
            case R.id.menu25:
                if(!item.isCheckable()){
                    item.setChecked(true);
                    feedLimit = 15 + feedLimit;
                    Log.d(TAG, "onOpionsItemSelected: " + item.getTitle() + "ajustando feedLimit para " + feedLimit);
                }else{
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "feeLimit não mudou. ");
                }
                break;
            default:
                return super.onOptionsItemSelected(item);//caso haja um sub menu esta op. é chamada!
        }

        downloadUrl(String.format(feedUrl, feedType, feedLimit)); //funciona como sprintf do C
        return true;
    }

    private void downloadUrl(String feedUrl){
        Log.d(TAG, "downloadUrl: iniciando a AsyncTask");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(feedUrl);
        Log.d(TAG, "downloadUrl: terminou.");
    }

    //esta classe >>> DownloadData estende a classe genérica AsyncTask que recebe 3 parametros<1º,2º,3º>
    //1º parametro - dado passado para a classe - uma URL - String
    //2º parâmetro - dados referente a progresso - ignoramos aqui - void
    //3º parametro - tipo de resultado(o que ele irá retornar) - no nosso caso uma String
    private  class DownloadData extends AsyncTask<String, Void, String>{
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
          //  Log.d(TAG, "onPostExecute: o parâmetro recebido é " + s);
            ParseApplications parser = new ParseApplications();
            parser.parse(s);
            //agora o objeto que o parse gerou e jogar para o adapter View e carregar para o listView
            //método abaixo comentado a fim de cancelar a apresentação na tela do FeedAdapter!

            //
            //método que puxa os dados da classe FeedAdapter
            FeedAdapter feedAdapter = new FeedAdapter(
                    MainActivity.this, R.layout.list_records, parser.getApplications()
            );
            ///
/*
             // Método que puxa os dados da classe FeeImageAdapter
            FeedImageAdapter feedAdapter = new FeedImageAdapter(
                   MainActivity.this, R.layout.list_records_images, parser.getApplications()
           );
*/
         //   rssListView.setAdapter(feedAdapter);
            rssListView.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: recebeu " + strings[0]);
            //String pokeFeed = downloadJSON(strings[0];//substituido por>>String contents = downloadContents(strings[0]);
            String contents = downloadContents(strings[0]);
            if(contents == null){
                Log.e(TAG, "doInbackground: Erro baixando dados. ");
            }
            return contents;
        }


        //downloadContents receberá por parametro a URL
        private String downloadContents(String urlPath){
            //String Builder é mais eficiente para concatenar strings que usar Ex: String s = ""; s +="";
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlPath); //abre uma conexão HTTP
                //após criado o objeto de URL, agora será usado para estabelecer uma conexão
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();//aqui espero conexão HTTP, caso contrário será tratado abaixo
                int responseCode = connection.getResponseCode(); // aqui pego cod de resposta para ter controle
                Log.d(TAG, "downloadContents: O código de resposta foi: " + responseCode);
               /* InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // tudo esse cod. pode ser sbst. pelo comando abaixo
                BufferedReader reader = new BufferedReader(inputStreamReader);*/
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead; // quantidade de caracteres lida
                char[] inputBuffer = new char[500];

                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0 ){
                        break;
                    }
                    if(charsRead > 0){
                        //obs: String.copyValueOf é tipo strcpy do "C"
                        // copie o conteudo do inputBuffer para dentro de result.
                        result.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return result.toString();
            }catch (MalformedURLException ex){
                Log.e(TAG, "downloadContens: URL inválida: " + ex.getMessage());
            } catch (IOException ex){
                Log.e(TAG, "downloadContents: IOException ao ler os dados:  " + ex.getMessage());
            }catch (SecurityException ex){
                Log.e(TAG, "downloadContents: Exceção de segurança. Falta permissão? " + ex.getMessage());

            }

            return null;
        }
    }
}
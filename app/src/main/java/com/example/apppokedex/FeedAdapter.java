package com.example.apppokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, @NonNull List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = layoutInflater.inflate(layoutResource, parent,  false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getNum());
        viewHolder.tvSummary.setText(currentApp.getHeight());
        viewHolder.tvweight.setText(currentApp.getWeight());
        // construir a string dos tipos
        String types = "";
        for (String t : currentApp.getType()) {
            types += t + '\n';
        }

        viewHolder.tvtype.setText(types);


      new FeedAdapter.DownloadImageTask(viewHolder.ivImageApp).execute(currentApp.getImg());

        return convertView;


    }

    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        final TextView tvweight;
        final TextView tvtype;
        final ImageView ivImageApp;

        ViewHolder(View v){
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
            this.tvweight = v.findViewById(R.id.tvweight);
            this.tvtype = v.findViewById(R.id.tvtype);
            this.ivImageApp = v.findViewById(R.id.ivImageApp);
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask (ImageView bmImage){
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];

            Bitmap bmp = null;
            try{
                URL urlObj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.connect();
                int response = conn.getResponseCode(); // acontece que essas imagens fazem um redirect! o código é 302

                if (response != HttpURLConnection.HTTP_OK) { // a resposta tem que ser OK (200)
                    if (response == HttpURLConnection.HTTP_MOVED_TEMP ||
                            response == HttpURLConnection.HTTP_MOVED_PERM ||
                            response == HttpURLConnection.HTTP_SEE_OTHER) { // se tem um redirect
                        // pegamos a url correta e redirecionamos o cliente para essa url
                        String newUrl = conn.getHeaderField("Location");
                        conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    }
                }
                InputStream inputStream = conn.getInputStream();
                bmp = BitmapFactory.decodeStream(inputStream);
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

            return bmp;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            bmImage.setImageBitmap(bitmap);
        }

    }

}

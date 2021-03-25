package com.example.retrofit2stub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PictureAdapter extends BaseAdapter {

    Context context;
    Hit[] hits;
    MainActivity.PixabayAPI api;

    public PictureAdapter(Context context, Hit[] hits, MainActivity.PixabayAPI api) {
        this.context = context;
        this.hits = hits;
        this.api = api;
    }

    @Override
    public int getCount() {
        return hits.length;
    }

    @Override
    public Object getItem(int position) {
        return hits[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ImageView imageView = convertView.findViewById(R.id.img);
        TextView textView = convertView.findViewById(R.id.img_text);

        Hit hit = hits[position];
        textView.setText("ID: " + hit.id + "\nType:" + hit.type);

        Call<ResponseBody> getImage = api.getImage(hit.previewURL);

        Callback<ResponseBody> imageCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                imageView.setImageBitmap(bmp);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("mytag", "Response error: " + t.getLocalizedMessage());
            }
        };
        getImage.enqueue(imageCallback);

        return convertView;
    }
}

package com.example.retrofit2stub;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {

    String API_URL = "https://pixabay.com/";
    String q = "bad dog";
    String key = "18604852-305866ecafd55a05f88babcdb";
    String[] image_types = { "all", "photo", "illustration", "vector"};
    MainActivity mainActivity = this;
    String item;
    Bitmap bmp;

    EditText request;
    ListView listView;
    ImageView imageView;

    interface PixabayAPI {
        @GET("/api")
        Call<Response> search(@Query("q") String q, @Query("key") String key, @Query("image_type") String image_type);
        @GET()
        Call<ResponseBody> getImage (@Url String pictureURL);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        request = findViewById(R.id.text);
        listView = findViewById(R.id.listview);
        imageView = findViewById(R.id.image);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, image_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    public void startSearch(String text) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PixabayAPI api = retrofit.create(PixabayAPI.class);

        Retrofit noRetrofit = new Retrofit.Builder().baseUrl(API_URL).build();
        final PixabayAPI PixApi = noRetrofit.create(PixabayAPI.class);

        Call<Response> call = api.search(text, key, item);
        Log.d("mytag", "item: " + item);

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                Response r = response.body(); // получили ответ в виде объекта
                displayResults(r.hits);
                Log.d("mytag", "hits:" + r.hits.length); // сколько картинок нашлось
                PictureAdapter adapter = new PictureAdapter(mainActivity, r.hits, PixApi);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error loading data", Toast.LENGTH_SHORT);
                toast.show();
                Log.d("mytag", "Error: " + t.getLocalizedMessage());
            }
        };
        call.enqueue(callback); // ставим запрос в очередь

    }

    public void displayResults(Hit[] hits) {
        ImageView im = findViewById(R.id.image);
        im.setImageBitmap(bmp);
    }

    public void onSearchClick(View v) {
        EditText etSearch = findViewById(R.id.text);
        String text = etSearch.getText().toString();
        startSearch(text);
    }
}

package com.example.banggulo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggulo.Api.ApiRequest;
import com.example.banggulo.Api.RetroClient;
import com.example.banggulo.Model.DataModel;
import com.example.banggulo.Model.ResponseModel;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView gambar;
    TextView kondisi, suhu, delaysuhu,nama,kelembapan,kondisisuhu,ldr,lampu,delayldr,delaygambar;
    private BroadcastReceiver minuteUpdateReceiver;
    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    private static final Handler tickHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gambar = (ImageView) findViewById(R.id.gambar);
        kondisi = (TextView) findViewById(R.id.kondisi);
        suhu = (TextView) findViewById(R.id.suhu);
        delaysuhu = (TextView) findViewById(R.id.delaysuhu);
        nama = (TextView) findViewById(R.id.nama);
        kelembapan = (TextView) findViewById(R.id.kelembapan);
        kondisisuhu = (TextView) findViewById(R.id.kondisisuhu);
        ldr = (TextView) findViewById(R.id.ldr);
        lampu = (TextView) findViewById(R.id.lampu);
        delayldr = (TextView) findViewById(R.id.delaylampu);
        delaygambar = (TextView) findViewById(R.id.delaygambar);
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                ApiRequest api = RetroClient.getClient().create(ApiRequest.class);
                Call<ResponseModel> getData = api.lihat();
                getData.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        Log.d("RETRO", "RESPONSE" +response.body().getKode());
                        ResponseModel res = response.body();
                        List<DataModel> user = res.getResult();
                        if (res.getKode().equals("1")){
                            final String imgURL  = user.get(0).getGambar();
//                            Toast.makeText(MainActivity.this, user.get(0).getGambar(), Toast.LENGTH_LONG).show();
                                kondisi.setText(user.get(0).getKondisi());
                                nama.setText(user.get(0).getNama());
                                suhu.setText(user.get(0).getSuhu()+" (Celcius)");
                                kelembapan.setText(user.get(0).getKelembapan()+" (Humid)");
                                kondisisuhu.setText(user.get(0).getKondisisuhu());
                                delaysuhu.setText(user.get(0).getDelaysuhu()+" (Detik)");
                                ldr.setText(user.get(0).getLdr()+" (LDR)");
                                lampu.setText(user.get(0).getLampu()+" (Lampu)");
                                delayldr.setText(user.get(0).getDelayldr()+" (Detik)");
                                delaygambar.setText(user.get(0).getDelaygambar()+" (Detik)");
                            new DownLoadImageTask(gambar).execute(imgURL);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Gagal", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.e(TAG,"error" + t.getMessage());
                    }
                });
                handler.postDelayed( this, 1000 );
            }
        }, 1000 );

    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }

    }

//    public void startMinuteUpdater() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_TIME_TICK);
//        minuteUpdateReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                ApiRequest api = RetroClient.getClient().create(ApiRequest.class);
//                Call<ResponseModel> getData = api.lihat();
//                getData.enqueue(new Callback<ResponseModel>() {
//                    @Override
//                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
//                        Log.d("RETRO", "RESPONSE" +response.body().getKode());
//                        ResponseModel res = response.body();
//                        List<DataModel> user = res.getResult();
//                        if (res.getKode().equals("1")){
//                            final String imgURL  = user.get(0).getGambar();
//                            Toast.makeText(MainActivity.this, user.get(0).getGambar(), Toast.LENGTH_LONG).show();
////                    kondisi.setText(user.get(0).getDelaysuhu());
//
//                            new DownLoadImageTask(gambar).execute(imgURL);
//                        }
//                        else {
//                            Toast.makeText(MainActivity.this, "Gagal", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseModel> call, Throwable t) {
//                        Log.e(TAG,"error" + t.getMessage());
//                    }
//                });
//            }
//        };
//        registerReceiver(minuteUpdateReceiver, intentFilter);
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        startMinuteUpdater();
    }
    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(minuteUpdateReceiver);
    }
}

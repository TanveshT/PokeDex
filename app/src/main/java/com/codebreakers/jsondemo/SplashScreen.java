package com.codebreakers.jsondemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends AppCompatActivity {

    private GifImageView splashGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        splashGif = findViewById(R.id.splashScreenImage);

        try{
            InputStream inputStream = getAssets().open("loadingPokeball.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            splashGif.setBytes(bytes);
            splashGif.startAnimation();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this,MainActivity.class));
                SplashScreen.this.finish();
            }
        },3000);
    }
}

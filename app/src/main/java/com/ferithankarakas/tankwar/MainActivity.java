package com.ferithankarakas.tankwar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isMute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        TextView scoretxt = findViewById(R.id.highscore);
        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        scoretxt.setText("En yüksek skor : " + prefs.getInt("highscore", 0));

        isMute = prefs.getBoolean("isMute", false);

        ImageView volumecontrol = findViewById(R.id.volumectrl);

        if (isMute)
            volumecontrol.setImageResource(R.drawable.ic_baseline_volume_off_24);
        else
            volumecontrol.setImageResource(R.drawable.ic_baseline_volume_up_24);

        volumecontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMute = !isMute;
                if (isMute)
                    volumecontrol.setImageResource(R.drawable.ic_baseline_volume_off_24);
                else
                    volumecontrol.setImageResource(R.drawable.ic_baseline_volume_up_24);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMute", isMute);
                editor.apply();
            }
        });

    }


}
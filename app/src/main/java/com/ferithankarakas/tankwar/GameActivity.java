package com.ferithankarakas.tankwar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        gameView = new GameView(GameActivity.this, point.x, point.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.Pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.Resume();
    }
}